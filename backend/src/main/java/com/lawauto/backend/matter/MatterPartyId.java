package com.lawauto.backend.matter;

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
    private UUID matterId;
    private UUID partyId;
    private UUID roleId;
}
