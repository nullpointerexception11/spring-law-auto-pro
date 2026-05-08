package com.lawauto.backend.matter;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    private String caseNumber;

    private String judgeName;

    private String clerkName;

    @Column(columnDefinition = "text")
    private String decisionSummary;

    private LocalDate decisionDate;

    private LocalDate appealDeadline;

    @Builder.Default
    private Boolean appealFiled = false;

    private LocalDate finalJudgmentDate;
}
