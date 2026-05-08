package com.lawauto.backend.petition;

import com.lawauto.backend.org.Org;
import com.lawauto.backend.storage.FileObject;
import com.lawauto.backend.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"PetitionTemplate\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetitionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isArchived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rollbackFromId")
    private PetitionTemplate rollbackFrom;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private String structureJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "templateFileId")
    private FileObject templateFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId", nullable = false)
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
