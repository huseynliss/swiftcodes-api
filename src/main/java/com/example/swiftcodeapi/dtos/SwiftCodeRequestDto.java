package com.example.swiftcodeapi.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCodeRequestDto {

    private String address;

    @NotBlank(message = "Bank name is required")
    @Size(max = 100, message = "Bank name must be less than 100 characters")
    private String bankName;

    @NotBlank(message = "Country ISO2 code is required")
    @Size(min = 2, max = 2, message = "Country ISO2 code must be 2 characters")
    private String countryISO2;

    @NotBlank(message = "Country name is required")
    @Size(max = 50, message = "Country name must be less than 50 characters")
    private String countryName;

    @NotNull(message = "Headquarter flag is required")
    private Boolean isHeadquarter;

    @NotBlank(message = "SWIFT code is required")
    @Pattern(
            regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$",
            message = "Invalid SWIFT code format"
    )
    private String swiftCode;

    @AssertTrue(message = "If headquarters, SWIFT code must end with 'XXX'.")
    private boolean isSwiftCodeValidForHeadquarter() {
        return !isHeadquarter || swiftCode.endsWith("XXX");
    }

    @AssertTrue(message = "If not headquarters, SWIFT code must NOT end with 'XXX'.")
    private boolean isSwiftCodeValidForNonHeadquarter() {
        return isHeadquarter || !swiftCode.endsWith("XXX");
    }

}
