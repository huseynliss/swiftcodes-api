package com.example.swiftcodeapi.service;

import com.example.swiftcodeapi.exception.ResourceNotFoundException;
import com.example.swiftcodeapi.model.SwiftCode;
import com.example.swiftcodeapi.repository.SwiftCodeRepository;
import com.example.swiftcodeapi.dtos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SwiftCodeServiceIntegrationTest {

    @Autowired
    private SwiftCodeService swiftCodeService;

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    private SwiftCode sampleSwiftCode;

    @BeforeEach
    void setUp() {
        // Test için örnek Swift Code ekle
        sampleSwiftCode = SwiftCode.builder().swiftCode("CHASUSXXXX").bankName("JPMORGAN CHASE").address("270 Park Avenue").countryISO2("US").countryName("United States").isHeadquarter(true).headquarterCode(null).build();

        swiftCodeRepository.save(sampleSwiftCode);
    }

    @Test
    void testGetSwiftCodeDetails() {
        SwiftCodeResponseDto responseDto = swiftCodeService.getSwiftCodeDetails("CHASUSXXXX");

        assertNotNull(responseDto);
        assertEquals("CHASUSXXXX", responseDto.getSwiftCode());
        assertEquals("JPMORGAN CHASE", responseDto.getBankName());
        assertEquals("270 Park Avenue", responseDto.getAddress());
        assertTrue(responseDto.isHeadquarter());
        assertTrue(responseDto.getBranches().isEmpty()); // Headquarters'ta branch olmamalı
    }

    @Test
    void testGetSwiftCodeDetails_WithBranch() {
        SwiftCode branch = SwiftCode.builder().swiftCode("CHASUSNY11").bankName("JPMORGAN CHASE NY").address("1 New York Plaza").countryISO2("US").countryName("United States").isHeadquarter(true).headquarterCode(null).build();

        swiftCodeRepository.save(branch);

        SwiftCodeResponseDto responseDto = swiftCodeService.getSwiftCodeDetails("CHASUSNY11");

        assertNotNull(responseDto);
        assertEquals(0, responseDto.getBranches().size());
        assertEquals("CHASUSNY11", responseDto.getSwiftCode());
    }

    @Test
    void testGetSwiftCodesByCountry() {
        SwiftCode anotherSwiftCode = SwiftCode.builder().swiftCode("HSBCGB2L").bankName("HSBC").address("8 Canada Square").countryISO2("GB").countryName("United Kingdom").isHeadquarter(true).headquarterCode(null).build();

        swiftCodeRepository.save(anotherSwiftCode);

        CountrySwiftCodesResponseDto responseDto = swiftCodeService.getSwiftCodesByCountry("GB");

        assertNotNull(responseDto);
        assertEquals("GB", responseDto.getCountryISO2());
        assertEquals("United Kingdom", responseDto.getCountryName());
        assertEquals(1, responseDto.getSwiftCodes().size());
    }

    @Test
    void testAddSwiftCode() {
        SwiftCodeRequestDto requestDto = SwiftCodeRequestDto.builder().swiftCode("HSBCGB2L").bankName("HSBC").address("8 Canada Square").countryISO2("GB").countryName("United Kingdom").build();

        MessageResponseDto responseDto = swiftCodeService.addSwiftCode(requestDto);

        assertNotNull(responseDto);
        assertEquals("Swift code added successfully", responseDto.getMessage());

        assertTrue(swiftCodeRepository.existsById("HSBCGB2L"));
    }

    @Test
    void testDeleteSwiftCode() {
        swiftCodeService.deleteSwiftCode("CHASUSXXXX");

        assertFalse(swiftCodeRepository.existsById("CHASUSXXXX"));
    }

    @Test
    void testDeleteSwiftCode_NotFound() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> swiftCodeService.deleteSwiftCode("INVALIDSWIFT"));

        assertEquals("Swift code not found: INVALIDSWIFT", exception.getMessage());
    }
}
