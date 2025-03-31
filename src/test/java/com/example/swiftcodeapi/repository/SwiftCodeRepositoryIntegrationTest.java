package com.example.swiftcodeapi.repository;

import com.example.swiftcodeapi.model.SwiftCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class SwiftCodeRepositoryIntegrationTest {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Test
    void testSaveAndFindBySwiftCode() {
        SwiftCode swiftCode = SwiftCode.builder()
                .swiftCode("BANKUS33XXX")
                .bankName("Test Bank")
                .address("123 Bank Street")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(true)
                .build();

        swiftCodeRepository.save(swiftCode);

        Optional<SwiftCode> found = swiftCodeRepository.findBySwiftCode("BANKUS33XXX");
        assertThat(found).isPresent();
        assertThat(found.get().getSwiftCode()).isEqualTo("BANKUS33XXX");
    }

    @Test
    void testFindByCountryISO2() {
        SwiftCode swiftCode1 = SwiftCode.builder()
                .swiftCode("BANKUS33XXX")
                .bankName("Test Bank 1")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(true)
                .build();

        SwiftCode swiftCode2 = SwiftCode.builder()
                .swiftCode("BANKUS33YYY")
                .bankName("Test Bank 2")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(false)
                .build();

        swiftCodeRepository.saveAll(List.of(swiftCode1, swiftCode2));

        List<SwiftCode> foundCodes = swiftCodeRepository.findByCountryISO2("US");
        assertThat(foundCodes).hasSize(2);
    }

    @Test
    void testFindBranchesByHeadquarterCode() {
        SwiftCode hq = SwiftCode.builder()
                .swiftCode("BANKUS33XXX")
                .bankName("Headquarter Bank")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(true)
                .build();

        SwiftCode branch = SwiftCode.builder()
                .swiftCode("BANKUS33YYZ")
                .bankName("Branch Bank")
                .countryISO2("US")
                .countryName("United States")
                .isHeadquarter(false)
                .headquarterCode("BANKUS33")
                .build();

        swiftCodeRepository.saveAll(List.of(hq, branch));

        List<SwiftCode> branches = swiftCodeRepository.findBranchesByHeadquarterCode("BANKUS33");
        assertThat(branches).hasSize(1);
        assertThat(branches.get(0).getSwiftCode()).isEqualTo("BANKUS33YYZ");
    }
}
