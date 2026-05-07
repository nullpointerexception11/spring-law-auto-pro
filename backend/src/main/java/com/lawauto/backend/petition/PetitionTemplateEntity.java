package com.lawauto.backend.petition;

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
@Table(name = "PetitionTemplate")
@Getter
@Setter
@NoArgsConstructor
public class PetitionTemplateEntity {
    @Id
    private UUID id;
    
    @Column(name = "orgId", nullable = false)
    private UUID orgId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private int version;
    
    @Column(name = "isActive", nullable = false)
    private boolean isActive;
    
    @Column(name = "structureJson", nullable = false)
    private String structureJson;
    
    @Column(name = "createdByUserId", nullable = false)
    private UUID createdByUserId;
    
    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;
}
