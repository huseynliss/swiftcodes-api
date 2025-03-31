package com.example.swiftcodeapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountrySwiftCodesResponseDto {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeDetailDto> swiftCodes;
}
