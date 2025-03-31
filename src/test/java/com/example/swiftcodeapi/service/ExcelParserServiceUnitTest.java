
package com.example.swiftcodeapi.service;

import com.example.swiftcodeapi.model.SwiftCode;
import com.example.swiftcodeapi.repository.SwiftCodeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ExcelParserServiceUnitTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private ApplicationReadyEvent applicationReadyEvent;

    @InjectMocks
    private ExcelParserService excelParserService;

    @TempDir
    Path tempDir;

    private File createTestExcelFile() throws Exception {
        File file = tempDir.resolve("C:\\\\Users\\\\SAHIN\\\\Downloads\\\\Interns_2025_SWIFT_CODES.xlsx").toFile();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Swift Codes");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"CountryISO2", "SWIFT Code", "Code Type", "Bank Name", "Address", "Town", "Country Name", "Time Zone"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            createDataRow(sheet.createRow(1), new String[]{"US", "CHASUSXXXX", "HQ", "JPMORGAN CHASE", "270 Park Avenue", "New York", "United States", "UTC-5"});
            createDataRow(sheet.createRow(2), new String[]{"US", "CHASUSNY11", "BRANCH", "JPMORGAN CHASE NY", "1 New York Plaza", "New York", "United States", "UTC-5"});
            createDataRow(sheet.createRow(3), new String[]{"GB", "HSBC1XXX", "HQ", "HSBC BANK", "8 Canada Square", "London", "United Kingdom", "UTC+0"});

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }

        return file;
    }

    private void createDataRow(Row row, String[] values) {
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
        }
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void parseExcelFileOnStartup_WithEmptyDatabase_ShouldImportSwiftCodes() throws Exception {
        File testFile = createTestExcelFile();
        ReflectionTestUtils.setField(excelParserService, "swiftCodesFilePath", testFile.getAbsolutePath());

        when(swiftCodeRepository.count()).thenReturn(0L);
        when(swiftCodeRepository.saveAll(anyList())).thenReturn(List.of());

        excelParserService.parseExcelFileOnStartup();

        verify(swiftCodeRepository, times(1)).count();
        verify(swiftCodeRepository, times(1)).saveAll(argThat(list -> {
            List<SwiftCode> codes = (List<SwiftCode>) list;
            return codes.size() == 3 &&
                    codes.stream().anyMatch(code -> code.getSwiftCode().equals("CHASUSXXXX")) &&
                    codes.stream().anyMatch(code -> code.getSwiftCode().equals("CHASUSNY11")) &&
                    codes.stream().anyMatch(code -> code.getSwiftCode().equals("HSBC1XXX"));
        }));
    }

    @Test
    void parseExcelFileOnStartup_WithExistingData_ShouldSkipImport() throws Exception {
        File testFile = createTestExcelFile();
        ReflectionTestUtils.setField(excelParserService, "swiftCodesFilePath", testFile.getAbsolutePath());

        when(swiftCodeRepository.count()).thenReturn(10L);

        excelParserService.parseExcelFileOnStartup();

        verify(swiftCodeRepository, times(1)).count();
        verify(swiftCodeRepository, never()).saveAll(anyList());
    }

    @Test
    void parseExcelFileOnStartup_WithNonExistentFile_ShouldHandleException() {
        String nonExistentFilePath = tempDir.resolve("non-existent-file.xlsx").toString();
        ReflectionTestUtils.setField(excelParserService, "swiftCodesFilePath", nonExistentFilePath);

        excelParserService.parseExcelFileOnStartup();

        verify(swiftCodeRepository, never()).saveAll(anyList());
    }
}