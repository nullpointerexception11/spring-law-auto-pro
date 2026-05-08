package com.lawauto.backend.cases;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CaseAssignmentId implements Serializable {
    @Column(name = "caseId")
    private UUID caseId;

    @Column(name = "userId")
    private UUID userId;
}
