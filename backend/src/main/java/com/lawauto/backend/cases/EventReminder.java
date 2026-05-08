package com.lawauto.backend.cases;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "\"EventReminder\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", nullable = false)
    private UniversalEvent event;

    @Column(nullable = false)
    private OffsetDateTime remindAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSent = false;

    private OffsetDateTime sentAt;
}
