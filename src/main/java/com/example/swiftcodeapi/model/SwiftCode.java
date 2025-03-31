package com.example.swiftcodeapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "swift_codes")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
//@NoArgsConstructor
@AllArgsConstructor
public class SwiftCode {

    @Id
    @Column(name = "swift_code", nullable = false, length = 11)
    private String swiftCode;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "address")
    private String address;

    @Column(name = "country_iso2", nullable = false, length = 2)
    private String countryISO2;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "is_headquarter", nullable = false)
    private boolean isHeadquarter;

    @Column(name = "headquarter_code", length = 8)
    private String headquarterCode;

    @Column(name = "time_zone")
    private String timeZone;

    @OneToMany(mappedBy = "headquarterCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SwiftCode> branches = new ArrayList<>();

}
