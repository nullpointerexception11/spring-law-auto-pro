package com.lawauto.backend.document;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * ACL entry that defines which principal (user or matter) can perform which
 * actions on a file.
 * The same table can store both user‑level and matter‑level permissions – the
 * type is
 * distinguished by the {@code principalType} column.
 */
@Entity
@Table(name = "file_permissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "file_id", "principal_id", "principal_type" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** The file this permission belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private FileMetadata file;

    /** UUID of the principal – either a user ID or a matter ID. */
    @Column(name = "principal_id", nullable = false, columnDefinition = "uuid")
    private UUID principalId;

    /** "USER" or "MATTER" */
    @Column(name = "principal_type", nullable = false, length = 8)
    private String principalType;

    /**
     * Set of allowed actions – e.g. READ, WRITE, DELETE.
     * Stored in a separate join table.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "file_permission_actions",
            joinColumns = @JoinColumn(name = "permission_id"))
    @Column(name = "action", length = 8)
    private Set<String> actions = new HashSet<>();

    /** Convenience constant values for principal types */
    public static final String USER = "USER";
    public static final String MATTER = "MATTER";
}
