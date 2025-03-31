package com.example.swiftcodeapi.repository;

import com.example.swiftcodeapi.model.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, String> {

    List<SwiftCode> findByCountryISO2(String countryISO2);

    @Query("SELECT s FROM SwiftCode s WHERE s.headquarterCode = ?1")
    List<SwiftCode> findBranchesByHeadquarterCode(String headquarterCode);

    Optional<SwiftCode> findBySwiftCode(String swiftCode);
}
