package com.lawauto.backend.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Client")
@Getter
@Setter
@NoArgsConstructor
public class Client {
    @Id
    private UUID id;

    @Column(name = "orgId", nullable = false)
    private UUID orgId;

    @Column(name = "fullName", nullable = false)
    private String fullName;

    private String phone;
    private String email;

    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;
}
