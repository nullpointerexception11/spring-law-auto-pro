package com.lawauto.backend.matter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"MatterParty\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatterParty {

    @EmbeddedId
    private MatterPartyId id = new MatterPartyId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matterId")
    @JoinColumn(name = "matterId", nullable = false)
    private Matter Matter;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partyId")
    @JoinColumn(name = "partyId", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "roleId", nullable = false)
    private MatterPartyRole role;
}
