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
public class MatterPartyId implements Serializable {
    @Column(name = "matterId")
    private UUID matterId;

    @Column(name = "partyId")
    private UUID partyId;
}
