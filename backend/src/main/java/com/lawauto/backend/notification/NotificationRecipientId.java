package com.lawauto.backend.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationRecipientId implements Serializable {
    @Column(name = "notificationId")
    private UUID notificationId;

    @Column(name = "userId")
    private UUID userId;
}
