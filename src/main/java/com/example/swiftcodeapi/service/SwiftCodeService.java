package com.example.swiftcodeapi.service;

import com.example.swiftcodeapi.exception.ResourceNotFoundException;
import com.example.swiftcodeapi.model.SwiftCode;
import com.example.swiftcodeapi.repository.SwiftCodeRepository;
import com.example.swiftcodeapi.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwiftCodeService {

    private final SwiftCodeRepository swiftCodeRepository;

    @Transactional(readOnly = true)
    public SwiftCodeResponseDto getSwiftCodeDetails(String swiftCode) {
        SwiftCode code = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new ResourceNotFoundException("Swift code not found: " + swiftCode));

        if (code.isHeadquarter()) {
            List<SwiftCode> branches = swiftCodeRepository.findBranchesByHeadquarterCode(code.getSwiftCode().substring(0, 8));

            List<BranchDto> branchDtos = branches.stream()
                    .map(branch -> BranchDto.builder()
                            .address(branch.getAddress())
                            .bankName(branch.getBankName())
                            .countryISO2(branch.getCountryISO2())
                            .isHeadquarter(false)
                            .swiftCode(branch.getSwiftCode())
                            .build())
                    .collect(Collectors.toList());

            return SwiftCodeResponseDto.builder()
                    .address(code.getAddress())
                    .bankName(code.getBankName())
                    .countryISO2(code.getCountryISO2())
                    .countryName(code.getCountryName())
                    .isHeadquarter(true)
                    .swiftCode(code.getSwiftCode())
                    .branches(branchDtos)
                    .build();
        } else {
            return SwiftCodeResponseDto.builder()
                    .address(code.getAddress())
                    .bankName(code.getBankName())
                    .countryISO2(code.getCountryISO2())
                    .countryName(code.getCountryName())
                    .isHeadquarter(false)
                    .swiftCode(code.getSwiftCode())
                    .build();
        }
    }

    @Transactional(readOnly = true)
    public CountrySwiftCodesResponseDto getSwiftCodesByCountry(String countryISO2) {
        List<SwiftCode> swiftCodes = swiftCodeRepository.findByCountryISO2(countryISO2.toUpperCase());

        if (swiftCodes.isEmpty()) {
            throw new ResourceNotFoundException("No swift codes found for country: " + countryISO2);
        }

        String countryName = swiftCodes.get(0).getCountryName();

        List<SwiftCodeDetailDto> swiftCodeDetails = swiftCodes.stream()
                .map(code -> SwiftCodeDetailDto.builder()
                        .address(code.getAddress())
                        .bankName(code.getBankName())
                        .countryISO2(code.getCountryISO2())
                        .isHeadquarter(code.isHeadquarter())
                        .swiftCode(code.getSwiftCode())
                        .build())
                .collect(Collectors.toList());

        return CountrySwiftCodesResponseDto.builder()
                .countryISO2(countryISO2.toUpperCase())
                .countryName(countryName)
                .swiftCodes(swiftCodeDetails)
                .build();
    }

    @Transactional
    public MessageResponseDto addSwiftCode(SwiftCodeRequestDto requestDto) {
        // Format country code and name to uppercase
        String formattedCountryISO2 = requestDto.getCountryISO2().toUpperCase();
        String formattedCountryName = requestDto.getCountryName().toUpperCase();

        // Determine if this is a headquarters or branch
        boolean isHeadquarter = requestDto.getSwiftCode().endsWith("XXX");

        // Build the Swift Code entity
        SwiftCode swiftCode = SwiftCode.builder()
                .swiftCode(requestDto.getSwiftCode())
                .bankName(requestDto.getBankName())
                .address(requestDto.getAddress())
                .countryISO2(formattedCountryISO2)
                .countryName(formattedCountryName)
                .isHeadquarter(isHeadquarter)
                .headquarterCode(isHeadquarter ? null : requestDto.getSwiftCode().substring(0, 8))
                .build();

        swiftCodeRepository.save(swiftCode);

        return MessageResponseDto.builder()
                .message("Swift code added successfully")
                .build();
    }

    @Transactional
    public MessageResponseDto deleteSwiftCode(String swiftCode) {
        SwiftCode code = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new ResourceNotFoundException("Swift code not found: " + swiftCode));

        swiftCodeRepository.delete(code);

        return MessageResponseDto.builder()
                .message("Swift code deleted successfully")
                .build();
    }
}
