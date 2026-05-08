package com.lawauto.backend.user;

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
public class UserOrgId implements Serializable {
    @Column(name = "userId")
    private UUID userId;

    @Column(name = "orgId")
    private UUID orgId;
}
