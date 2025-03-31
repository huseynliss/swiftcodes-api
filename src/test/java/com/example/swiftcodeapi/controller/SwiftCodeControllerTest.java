package com.example.swiftcodeapi.controller;

import com.example.swiftcodeapi.dtos.*;
import com.example.swiftcodeapi.service.SwiftCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SwiftCodeControllerTest {

    @Mock
    private SwiftCodeService swiftCodeService;

    @InjectMocks
    private SwiftCodeController swiftCodeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getSwiftCodeDetails_ShouldReturnSwiftCodeDetails() {
        String swiftCode = "BNPAFRPPXXX";
        SwiftCodeResponseDto expectedResponse = SwiftCodeResponseDto.builder()
                .swiftCode(swiftCode)
                .bankName("BNP PARIBAS")
                .countryISO2("FR")
                .countryName("FRANCE")
                .isHeadquarter(true)
                .build();

        when(swiftCodeService.getSwiftCodeDetails(swiftCode)).thenReturn(expectedResponse);

        ResponseEntity<SwiftCodeResponseDto> response = swiftCodeController.getSwiftCodeDetails(swiftCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(swiftCodeService, times(1)).getSwiftCodeDetails(swiftCode);
    }

    @Test
    void getSwiftCodesByCountry_ShouldReturnCountrySwiftCodes() {
        String countryISO2 = "FR";
        List<SwiftCodeDetailDto> swiftCodes = Arrays.asList(
                SwiftCodeDetailDto.builder()
                        .swiftCode("BNPAFRPPXXX")
                        .bankName("BNP PARIBAS")
                        .countryISO2(countryISO2)
                        .isHeadquarter(true)
                        .build(),
                SwiftCodeDetailDto.builder()
                        .swiftCode("SOGEFRPP")
                        .bankName("SOCIETE GENERALE")
                        .countryISO2(countryISO2)
                        .isHeadquarter(false)
                        .build()
        );

        CountrySwiftCodesResponseDto expectedResponse = CountrySwiftCodesResponseDto.builder()
                .countryISO2(countryISO2)
                .countryName("FRANCE")
                .swiftCodes(swiftCodes)
                .build();

        when(swiftCodeService.getSwiftCodesByCountry(countryISO2)).thenReturn(expectedResponse);

        ResponseEntity<CountrySwiftCodesResponseDto> response = swiftCodeController.getSwiftCodesByCountry(countryISO2);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(swiftCodeService, times(1)).getSwiftCodesByCountry(countryISO2);
    }

    @Test
    void addSwiftCode_ShouldCreateSwiftCode() {
        SwiftCodeRequestDto requestDto = SwiftCodeRequestDto.builder()
                .swiftCode("BNPAFRPPXXX")
                .bankName("BNP PARIBAS")
                .countryISO2("FR")
                .countryName("FRANCE")
                .isHeadquarter(true)
                .build();

        MessageResponseDto expectedResponse = MessageResponseDto.builder()
                .message("Swift code added successfully")
                .build();

        when(swiftCodeService.addSwiftCode(requestDto)).thenReturn(expectedResponse);

        ResponseEntity<MessageResponseDto> response = swiftCodeController.addSwiftCode(requestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(swiftCodeService, times(1)).addSwiftCode(requestDto);
    }

    @Test
    void deleteSwiftCode_ShouldDeleteSwiftCode() {
        String swiftCode = "BNPAFRPPXXX";
        MessageResponseDto expectedResponse = MessageResponseDto.builder()
                .message("Swift code deleted successfully")
                .build();

        when(swiftCodeService.deleteSwiftCode(swiftCode)).thenReturn(expectedResponse);

        ResponseEntity<MessageResponseDto> response = swiftCodeController.deleteSwiftCode(swiftCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(swiftCodeService, times(1)).deleteSwiftCode(swiftCode);
    }
}