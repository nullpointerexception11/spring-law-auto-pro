package com.lawauto.backend.storage;

import com.lawauto.backend.cases.Matter;
import com.lawauto.backend.org.Org;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"FileFolder\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgId", nullable = false)
    private Org org;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentFolderId")
    private FileFolder parentFolder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matterId")
    private Matter matter;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
