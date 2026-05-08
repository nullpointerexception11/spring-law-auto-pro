package com.lawauto.backend.cases;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "\"LitigationDetail\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LitigationDetail {

    @Id
    private UUID matterId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "matterId")
    private Matter matter;

    private String courtName;
    private String courtCity;
    private String degree;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isInsurance = false;

    @JdbcTypeCode(SqlTypes.JSON)
    private String insuranceDataJson;
}
