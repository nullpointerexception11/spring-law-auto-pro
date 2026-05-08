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
public class SecretaryAssignmentId implements Serializable {
    @Column(name = "secretaryUserId")
    private UUID secretaryUserId;

    @Column(name = "lawyerUserId")
    private UUID lawyerUserId;
}
