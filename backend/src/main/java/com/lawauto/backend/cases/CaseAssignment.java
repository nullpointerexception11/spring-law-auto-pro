package com.lawauto.backend.cases;

import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"CaseAssignment\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseAssignment {

    @EmbeddedId
    private CaseAssignmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("caseId")
    @JoinColumn(name = "caseId")
    private CaseEntity caseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "userId")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentRole role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;
}
