package com.example.swiftcodeapi.controller;

import com.example.swiftcodeapi.dtos.CountrySwiftCodesResponseDto;
import com.example.swiftcodeapi.dtos.MessageResponseDto;
import com.example.swiftcodeapi.dtos.SwiftCodeRequestDto;
import com.example.swiftcodeapi.dtos.SwiftCodeResponseDto;
import com.example.swiftcodeapi.service.SwiftCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/swift-codes")
@RequiredArgsConstructor
@Tag(name = "Swift Codes API", description = "API for managing SWIFT codes")
public class SwiftCodeController {

    private final SwiftCodeService swiftCodeService;

    @GetMapping("/{swiftCode}")
    @Operation(summary = "Get details of a specific SWIFT code", description = "Retrieve details of a SWIFT code by providing its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved SWIFT code details"),
            @ApiResponse(responseCode = "404", description = "SWIFT code not found")
    })
    public ResponseEntity<SwiftCodeResponseDto> getSwiftCodeDetails(@PathVariable String swiftCode) {
        return ResponseEntity.ok(swiftCodeService.getSwiftCodeDetails(swiftCode));
    }

    @GetMapping("/country/{countryISO2}")
    @Operation(summary = "Get all SWIFT codes for a specific country", description = "Retrieve a list of SWIFT codes for a given country.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved SWIFT codes"),
            @ApiResponse(responseCode = "404", description = "No SWIFT codes found for the country")
    })
    public ResponseEntity<CountrySwiftCodesResponseDto> getSwiftCodesByCountry(@PathVariable String countryISO2) {
        return ResponseEntity.ok(swiftCodeService.getSwiftCodesByCountry(countryISO2));
    }

    @PostMapping
    @Operation(summary = "Add a new SWIFT code", description = "Create a new SWIFT code entry in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "SWIFT code created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<MessageResponseDto> addSwiftCode(@Valid @RequestBody SwiftCodeRequestDto requestDto) {
        return new ResponseEntity<>(swiftCodeService.addSwiftCode(requestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{swiftCode}")
    @Operation(summary = "Delete a SWIFT code", description = "Remove a SWIFT code entry from the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SWIFT code deleted successfully"),
            @ApiResponse(responseCode = "404", description = "SWIFT code not found")
    })
    public ResponseEntity<MessageResponseDto> deleteSwiftCode(@PathVariable String swiftCode) {
        return ResponseEntity.ok(swiftCodeService.deleteSwiftCode(swiftCode));
    }
}
