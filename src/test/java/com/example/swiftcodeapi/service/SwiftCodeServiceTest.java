package com.example.swiftcodeapi.service;

import com.example.swiftcodeapi.exception.ResourceNotFoundException;
import com.example.swiftcodeapi.model.SwiftCode;
import com.example.swiftcodeapi.repository.SwiftCodeRepository;
import com.example.swiftcodeapi.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SwiftCodeServiceTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSwiftCodeDetails_WithHeadquarter_ShouldReturnDetailsWithBranches() {
        // Arrange
        String swiftCode = "HSBC1XXX";
        String headquarterCode = "HSBC1XXX".substring(0, 8);

        SwiftCode headquarter = SwiftCode.builder()
                .swiftCode(swiftCode)
                .bankName("HSBC BANK")
                .address("123 Main St")
                .countryISO2("GB")
                .countryName("UNITED KINGDOM")
                .isHeadquarter(true)
                .build();

        List<SwiftCode> branches = Arrays.asList(
                SwiftCode.builder()
                        .swiftCode("HSBC1GB1")
                        .bankName("HSBC LONDON")
                        .address("London Branch")
                        .countryISO2("GB")
                        .countryName("UNITED KINGDOM")
                        .isHeadquarter(false)
                        .headquarterCode(headquarterCode)
                        .build(),
                SwiftCode.builder()
                        .swiftCode("HSBC1GB2")
                        .bankName("HSBC MANCHESTER")
                        .address("Manchester Branch")
                        .countryISO2("GB")
                        .countryName("UNITED KINGDOM")
                        .isHeadquarter(false)
                        .headquarterCode(headquarterCode)
                        .build()
        );

        when(swiftCodeRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.of(headquarter));
        when(swiftCodeRepository.findBranchesByHeadquarterCode(swiftCode.substring(0, 8))).thenReturn(branches);

        SwiftCodeResponseDto result = swiftCodeService.getSwiftCodeDetails(swiftCode);

        assertNotNull(result);
        assertEquals(swiftCode, result.getSwiftCode());
        assertEquals(headquarter.getBankName(), result.getBankName());
        assertEquals(headquarter.getCountryISO2(), result.getCountryISO2());
        assertEquals(headquarter.getCountryName(), result.getCountryName());
        assertTrue(result.isHeadquarter());
        assertEquals(2, result.getBranches().size());

        verify(swiftCodeRepository, times(1)).findBySwiftCode(swiftCode);
        verify(swiftCodeRepository, times(1)).findBranchesByHeadquarterCode(swiftCode.substring(0, 8));
    }

    @Test
    void getSwiftCodeDetails_WithBranch_ShouldReturnDetailsWithoutBranches() {
        String swiftCode = "HSBC1GB1";

        SwiftCode branch = SwiftCode.builder()
                .swiftCode(swiftCode)
                .bankName("HSBC LONDON")
                .address("London Branch")
                .countryISO2("GB")
                .countryName("UNITED KINGDOM")
                .isHeadquarter(false)
                .headquarterCode("HSBC1XXX".substring(0, 8))
                .build();

        when(swiftCodeRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.of(branch));

        SwiftCodeResponseDto result = swiftCodeService.getSwiftCodeDetails(swiftCode);

        assertNotNull(result);
        assertEquals(swiftCode, result.getSwiftCode());
        assertEquals(branch.getBankName(), result.getBankName());
        assertEquals(branch.getCountryISO2(), result.getCountryISO2());
        assertEquals(branch.getCountryName(), result.getCountryName());
        assertFalse(result.isHeadquarter());
        assertNull(result.getBranches());

        verify(swiftCodeRepository, times(1)).findBySwiftCode(swiftCode);
        verify(swiftCodeRepository, never()).findBranchesByHeadquarterCode(any());
    }

    @Test
    void getSwiftCodeDetails_WithNonExistentCode_ShouldThrowException() {
        String swiftCode = "NONEXISTENT";
        when(swiftCodeRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            swiftCodeService.getSwiftCodeDetails(swiftCode);
        });

        assertEquals("Swift code not found: " + swiftCode, exception.getMessage());
        verify(swiftCodeRepository, times(1)).findBySwiftCode(swiftCode);
    }

    @Test
    void getSwiftCodesByCountry_ShouldReturnSwiftCodesForCountry() {
        String countryISO2 = "US";

        List<SwiftCode> swiftCodes = Arrays.asList(
                SwiftCode.builder()
                        .swiftCode("CHASUSXXXX")
                        .bankName("JPMORGAN CHASE")
                        .countryISO2(countryISO2)
                        .countryName("UNITED STATES")
                        .isHeadquarter(true)
                        .build(),
                SwiftCode.builder()
                        .swiftCode("BOAUS123")
                        .bankName("BANK OF AMERICA")
                        .countryISO2(countryISO2)
                        .countryName("UNITED STATES")
                        .isHeadquarter(false)
                        .build()
        );

        when(swiftCodeRepository.findByCountryISO2(countryISO2.toUpperCase())).thenReturn(swiftCodes);

        CountrySwiftCodesResponseDto result = swiftCodeService.getSwiftCodesByCountry(countryISO2);

        assertNotNull(result);
        assertEquals(countryISO2.toUpperCase(), result.getCountryISO2());
        assertEquals("UNITED STATES", result.getCountryName());
        assertEquals(2, result.getSwiftCodes().size());

        verify(swiftCodeRepository, times(1)).findByCountryISO2(countryISO2.toUpperCase());
    }

    @Test
    void getSwiftCodesByCountry_WithNoSwiftCodes_ShouldThrowException() {

        String countryISO2 = "ZZ";
        when(swiftCodeRepository.findByCountryISO2(countryISO2.toUpperCase())).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            swiftCodeService.getSwiftCodesByCountry(countryISO2);
        });

        assertEquals("No swift codes found for country: " + countryISO2, exception.getMessage());
        verify(swiftCodeRepository, times(1)).findByCountryISO2(countryISO2.toUpperCase());
    }

    @Test
    void addSwiftCode_WithHeadquarter_ShouldAddSwiftCode() {
        // Arrange
        SwiftCodeRequestDto requestDto = SwiftCodeRequestDto.builder()
                .swiftCode("BNPAFRPPXXX")
                .bankName("BNP PARIBAS")
                .countryISO2("fr")
                .countryName("France")
                .isHeadquarter(true)
                .address("16 Boulevard des Italiens")
                .build();

        when(swiftCodeRepository.save(any(SwiftCode.class))).thenAnswer(i -> i.getArgument(0));

        MessageResponseDto result = swiftCodeService.addSwiftCode(requestDto);

        assertNotNull(result);
        assertEquals("Swift code added successfully", result.getMessage());

        verify(swiftCodeRepository, times(1)).save(argThat(swiftCode ->
                swiftCode.getSwiftCode().equals(requestDto.getSwiftCode()) &&
                        swiftCode.getBankName().equals(requestDto.getBankName()) &&
                        swiftCode.getCountryISO2().equals(requestDto.getCountryISO2().toUpperCase()) &&
                        swiftCode.getCountryName().equals(requestDto.getCountryName().toUpperCase()) &&
                        swiftCode.isHeadquarter() == requestDto.getIsHeadquarter() &&
                        swiftCode.getHeadquarterCode() == null
        ));
    }

    @Test
    void addSwiftCode_WithBranch_ShouldAddSwiftCode() {
        // Arrange
        SwiftCodeRequestDto requestDto = SwiftCodeRequestDto.builder()
                .swiftCode("BNPAFRPP123")
                .bankName("BNP PARIBAS PARIS")
                .countryISO2("fr")
                .countryName("France")
                .isHeadquarter(false)
                .address("Paris Branch")
                .build();

        when(swiftCodeRepository.save(any(SwiftCode.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        MessageResponseDto result = swiftCodeService.addSwiftCode(requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Swift code added successfully", result.getMessage());

        verify(swiftCodeRepository, times(1)).save(argThat(swiftCode ->
                swiftCode.getSwiftCode().equals(requestDto.getSwiftCode()) &&
                        swiftCode.getBankName().equals(requestDto.getBankName()) &&
                        swiftCode.getCountryISO2().equals(requestDto.getCountryISO2().toUpperCase()) &&
                        swiftCode.getCountryName().equals(requestDto.getCountryName().toUpperCase()) &&
                        swiftCode.isHeadquarter() == requestDto.getIsHeadquarter() &&
                        swiftCode.getHeadquarterCode().equals(requestDto.getSwiftCode().substring(0, 8))
        ));
    }

    @Test
    void deleteSwiftCode_ShouldDeleteSwiftCode() {
        String swiftCode = "BNPAFRPPXXX";
        SwiftCode code = SwiftCode.builder()
                .swiftCode(swiftCode)
                .bankName("BNP PARIBAS")
                .countryISO2("FR")
                .countryName("FRANCE")
                .isHeadquarter(true)
                .build();

        when(swiftCodeRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.of(code));
        doNothing().when(swiftCodeRepository).delete(code);

        MessageResponseDto result = swiftCodeService.deleteSwiftCode(swiftCode);

        assertNotNull(result);
        assertEquals("Swift code deleted successfully", result.getMessage());

        verify(swiftCodeRepository, times(1)).findBySwiftCode(swiftCode);
        verify(swiftCodeRepository, times(1)).delete(code);
    }

    @Test
    void deleteSwiftCode_WithNonExistentCode_ShouldThrowException() {
        String swiftCode = "NONEXISTENT";
        when(swiftCodeRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            swiftCodeService.deleteSwiftCode(swiftCode);
        });

        assertEquals("Swift code not found: " + swiftCode, exception.getMessage());
        verify(swiftCodeRepository, times(1)).findBySwiftCode(swiftCode);
        verify(swiftCodeRepository, never()).delete(any());
    }
}
