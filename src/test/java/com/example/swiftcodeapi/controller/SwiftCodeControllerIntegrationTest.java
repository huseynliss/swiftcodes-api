package com.example.swiftcodeapi.controller;

import com.example.swiftcodeapi.controller.SwiftCodeController;
import com.example.swiftcodeapi.dtos.*;
import com.example.swiftcodeapi.service.SwiftCodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class SwiftCodeControllerIntegrationTest {

    private MockMvc mockMvc;

    @Mock
    private SwiftCodeService swiftCodeService;

    @InjectMocks
    private SwiftCodeController swiftCodeController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(swiftCodeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetSwiftCodeDetails() throws Exception {
        SwiftCodeResponseDto responseDto = SwiftCodeResponseDto.builder()
                .address("123 Bank Street")
                .bankName("Test Bank")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(true)
                .swiftCode("BANKUS33XXX")
                .build();

        when(swiftCodeService.getSwiftCodeDetails("BANKUS33XXX")).thenReturn(responseDto);

        mockMvc.perform(get("/v1/swift-codes/BANKUS33XXX"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.swiftCode").value("BANKUS33XXX"))
                .andExpect(jsonPath("$.bankName").value("Test Bank"))
                .andExpect(jsonPath("$.countryISO2").value("US"))
                .andExpect(jsonPath("$.countryName").value("United States"))
                .andExpect(jsonPath("$.isHeadquarter").value(true));
    }

    @Test
    void testGetSwiftCodesByCountry() throws Exception {
        SwiftCodeDetailDto swiftCodeDetailDto1 = SwiftCodeDetailDto.builder()
                .address("123 Bank Street")
                .bankName("Test Bank")
                .countryISO2("US")
                .isHeadquarter(true)
                .swiftCode("BANKUS33XXX")
                .build();

        SwiftCodeDetailDto swiftCodeDetailDto2 = SwiftCodeDetailDto.builder()
                .address("456 Bank Avenue")
                .bankName("Another Bank")
                .countryISO2("US")
                .isHeadquarter(false)
                .swiftCode("BANKUS22XXX")
                .build();

        List<SwiftCodeDetailDto> swiftCodeDetails = Arrays.asList(swiftCodeDetailDto1, swiftCodeDetailDto2);

        CountrySwiftCodesResponseDto responseDto = CountrySwiftCodesResponseDto.builder()
                .countryISO2("US")
                .countryName("United States")
                .swiftCodes(swiftCodeDetails)
                .build();

        when(swiftCodeService.getSwiftCodesByCountry("US")).thenReturn(responseDto);

        mockMvc.perform(get("/v1/swift-codes/country/US"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.countryISO2").value("US"))
                .andExpect(jsonPath("$.countryName").value("United States"))
                .andExpect(jsonPath("$.swiftCodes.length()").value(2));
    }

    @Test
    void testAddSwiftCode() throws Exception {
        SwiftCodeRequestDto requestDto = SwiftCodeRequestDto.builder()
                .address("123 Bank Street")
                .bankName("Test Bank")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(true)
                .swiftCode("BANKUS33XXX")
                .build();

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("SWIFT code added successfully")
                .build();

        when(swiftCodeService.addSwiftCode(any(SwiftCodeRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("SWIFT code added successfully"));
    }

    @Test
    void testDeleteSwiftCode() throws Exception {
        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("SWIFT code deleted successfully")
                .build();

        when(swiftCodeService.deleteSwiftCode("BANKUS33XXX")).thenReturn(responseDto);

        mockMvc.perform(delete("/v1/swift-codes/BANKUS33XXX"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("SWIFT code deleted successfully"));
    }
}
