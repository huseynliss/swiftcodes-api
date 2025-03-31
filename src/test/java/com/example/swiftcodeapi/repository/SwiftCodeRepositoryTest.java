package com.example.swiftcodeapi.repository;

import com.example.swiftcodeapi.model.SwiftCode;
import org.junit.jupiter.api.BeforeEach;
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
class SwiftCodeRepositoryTest {

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @BeforeEach
    void setUp() {
        SwiftCode swiftCode1 = SwiftCode.builder()
                .swiftCode("AAISALTRXXX")
                .address("TIRANA")
                .bankName("UNITED BANK OF ALBANIA SH.A")
                .countryISO2("AL")
                .countryName("ALBANIA")
                .isHeadquarter(true)
                .build();

        SwiftCode swiftCode2 = SwiftCode.builder()
                .swiftCode("ABIEBGS1XXX")
                .address("VARNA")
                .bankName("ABV INVESTMENTS LTD")
                .countryISO2("BG")
                .countryName("BULGARIA")
                .isHeadquarter(true)
                .build();

        swiftCodeRepository.save(swiftCode1);
        swiftCodeRepository.save(swiftCode2);
    }

    @Test
    void testFindByCountryISO2() {
        List<SwiftCode> results = swiftCodeRepository.findByCountryISO2("AL");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSwiftCode()).isEqualTo("AAISALTRXXX");
    }

    @Test
    void testFindBranchesByHeadquarterCode() {
        List<SwiftCode> results = swiftCodeRepository.findBranchesByHeadquarterCode("AAISALTRXXX");
        assertThat(results).isNotNull();
    }

    @Test
    void testFindBySwiftCode() {
        Optional<SwiftCode> result = swiftCodeRepository.findBySwiftCode("ABIEBGS1XXX");
        assertThat(result).isPresent();
        assertThat(result.get().getBankName()).isEqualTo("ABV INVESTMENTS LTD");
    }
}
