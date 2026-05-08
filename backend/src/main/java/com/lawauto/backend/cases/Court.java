package com.lawauto.backend.cases;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Court\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId")
    private Org org; // Null for System Defaults

    @Column(nullable = false)
    private String name;

    private String city;
    private String degree;
    private String uyapCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updatedByUserId")
    private User updatedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
