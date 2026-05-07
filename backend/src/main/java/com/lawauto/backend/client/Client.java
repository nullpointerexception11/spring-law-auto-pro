package com.lawauto.backend.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"Client\"")
public class Client {
    @Id
    private UUID id;

    @Column(name = "orgId", nullable = false)
    private UUID orgId;

    @Column(nullable = false)
    private String fullName;

    private String phone;
    private String email;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    public UUID getId() { return id; }
    public UUID getOrgId() { return orgId; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
}
