package com.lawauto.backend.matter;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "litigation_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LitigationDetail {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private Matter matter;

    @Column(name = "court_name")
    private String courtName;

    @Column(name = "case_number")
    private String caseNumber;

    @Column(name = "judge_name")
    private String judgeName;

    @Column(name = "clerk_name")
    private String clerkName;

    @Column(name = "decision_summary", columnDefinition = "text")
    private String decisionSummary;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @Column(name = "appeal_deadline")
    private LocalDate appealDeadline;

    @Builder.Default
    @Column(name = "appeal_filed")
    private Boolean appealFiled = false;

    @Column(name = "final_judgment_date")
    private LocalDate finalJudgmentDate;
}
