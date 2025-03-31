package com.example.swiftcodeapi.controller;

import com.example.swiftcodeapi.dtos.CountrySwiftCodesResponseDto;
import com.example.swiftcodeapi.dtos.MessageResponseDto;
import com.example.swiftcodeapi.dtos.SwiftCodeRequestDto;
import com.example.swiftcodeapi.dtos.SwiftCodeResponseDto;
import com.example.swiftcodeapi.service.SwiftCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping("/{swift-code}")
    @Operation(summary = "Get details of a specific SWIFT code")
    public ResponseEntity<SwiftCodeResponseDto> getSwiftCodeDetails(@PathVariable("swift-code") String swiftCode) {
        return ResponseEntity.ok(swiftCodeService.getSwiftCodeDetails(swiftCode));
    }

    @GetMapping("/country/{countryISO2code}")
    @Operation(summary = "Get all SWIFT codes for a specific country")
    public ResponseEntity<CountrySwiftCodesResponseDto> getSwiftCodesByCountry(@PathVariable String countryISO2code) {
        return ResponseEntity.ok(swiftCodeService.getSwiftCodesByCountry(countryISO2code));
    }

    @PostMapping
    @Operation(summary = "Add a new SWIFT code")
    public ResponseEntity<MessageResponseDto> addSwiftCode(@Valid @RequestBody SwiftCodeRequestDto requestDto) {
        return new ResponseEntity<>(swiftCodeService.addSwiftCode(requestDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{swift-code}")
    @Operation(summary = "Delete a SWIFT code")
    public ResponseEntity<MessageResponseDto> deleteSwiftCode(@PathVariable("swift-code") String swiftCode) {
        return ResponseEntity.ok(swiftCodeService.deleteSwiftCode(swiftCode));
    }
}
