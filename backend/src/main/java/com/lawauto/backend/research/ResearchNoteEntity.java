package com.lawauto.backend.research;

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
@Table(name = "ResearchNote")
@Getter
@Setter
@NoArgsConstructor
public class ResearchNoteEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private UUID researchSessionId;
    @Column(nullable = false)
    private UUID userId;
    @Column(nullable = false)
    private String noteText;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
