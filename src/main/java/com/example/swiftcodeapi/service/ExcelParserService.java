package com.example.swiftcodeapi.service;

import com.example.swiftcodeapi.model.SwiftCode;
import com.example.swiftcodeapi.repository.SwiftCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelParserService {

    private final SwiftCodeRepository swiftCodeRepository;

    @Value("${swiftcode.import.filepath}")
    private String swiftCodesFilePath;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void parseExcelFileOnStartup() {
        try {
            log.info("Starting to parse Swift Codes Excel file: {}", swiftCodesFilePath);
            List<SwiftCode> swiftCodes = parseExcelFile();

            if (swiftCodeRepository.count() == 0) {
                swiftCodeRepository.saveAll(swiftCodes);
                log.info("Successfully saved {} Swift Codes to the database", swiftCodes.size());
            } else {
                log.info("Database already contains Swift Codes, skipping import");
            }
        } catch (Exception e) {
            log.error("Error parsing Swift Codes Excel file: {}", swiftCodesFilePath, e);
        }
    }

    private List<SwiftCode> parseExcelFile() throws IOException {
        List<SwiftCode> swiftCodes = new ArrayList<>();
        File file = new File(swiftCodesFilePath);

        if (!file.exists()) {
            throw new IOException("Swift Codes file not found: " + swiftCodesFilePath);
        }

        try (FileInputStream is = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                throw new IOException("Excel file is empty: " + swiftCodesFilePath);
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String countryISO2 = getStringCellValue(row.getCell(0));
                String swiftCode = getStringCellValue(row.getCell(1));
                String codeType = getStringCellValue(row.getCell(2));
                String bankName = getStringCellValue(row.getCell(3));
                String address = getStringCellValue(row.getCell(4));
                String townName = getStringCellValue(row.getCell(5));
                String countryName = getStringCellValue(row.getCell(6));
                String timeZone = getStringCellValue(row.getCell(7));

                if (swiftCode == null || swiftCode.isEmpty()) {
                    log.warn("Skipping row {} due to missing SWIFT code", row.getRowNum());
                    continue;
                }

                countryISO2 = (countryISO2 != null) ? countryISO2.toUpperCase() : "";
                countryName = (countryName != null) ? countryName.toUpperCase() : "";
                boolean isHeadquarter = swiftCode.endsWith("XXX");

                String fullAddress = (address != null && !address.isEmpty()) ? address : "";
                if (townName != null && !townName.isEmpty()) {
                    fullAddress = fullAddress.isEmpty() ? townName : fullAddress + ", " + townName;
                }

                SwiftCode code = SwiftCode.builder()
                        .swiftCode(swiftCode)
                        .bankName(bankName)
                        .address(fullAddress)
                        .countryISO2(countryISO2)
                        .countryName(countryName)
                        .isHeadquarter(isHeadquarter)
                        .timeZone(timeZone)
                        .build();

                if (!isHeadquarter && swiftCode.length() >= 8) {
                    code.setHeadquarterCode(swiftCode.substring(0, 8));
                }

                swiftCodes.add(code);
            }
        }
        log.info("Parsed {} SWIFT codes from Excel file", swiftCodes.size());
        return swiftCodes;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }
}
