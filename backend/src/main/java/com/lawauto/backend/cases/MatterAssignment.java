package com.lawauto.backend.cases;

import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"MatterAssignment\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatterAssignment {

    @EmbeddedId
    private MatterAssignmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("matterId")
    @JoinColumn(name = "matterId")
    private Matter matter;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AssignmentRole role = AssignmentRole.LEAD;
}
