package com.lawauto.backend.cases;

import com.lawauto.backend.client.Party;
import com.lawauto.backend.client.PartyRole;
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
    private MatterPartyId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matterId")
    @JoinColumn(name = "matterId")
    private Matter matter;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partyId")
    @JoinColumn(name = "partyId")
    private Party party;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PartyRole role = PartyRole.CLIENT;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
