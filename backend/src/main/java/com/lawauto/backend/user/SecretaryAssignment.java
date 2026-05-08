package com.lawauto.backend.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"SecretaryAssignment\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecretaryAssignment {

    @EmbeddedId
    private SecretaryAssignmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("secretaryUserId")
    @JoinColumn(name = "secretaryUserId")
    private User secretary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lawyerUserId")
    @MapsId("lawyerUserId")
    private User lawyer;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt;
}
