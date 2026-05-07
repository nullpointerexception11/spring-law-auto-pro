package com.lawauto.backend.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Role\"")
public class RoleEntity {
    @Id
    private UUID id;

    @Column(name = "orgId", nullable = false)
    private UUID orgId;

    @Enumerated(EnumType.STRING)
    @Column(name = "key", nullable = false)
    private RoleKey key;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getOrgId() { return orgId; }
    public void setOrgId(UUID orgId) { this.orgId = orgId; }
    public RoleKey getKey() { return key; }
    public void setKey(RoleKey key) { this.key = key; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
