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
public class CaseClientId implements Serializable {
    @Column(name = "caseId")
    private UUID caseId;

    @Column(name = "clientId")
    private UUID clientId;
}
