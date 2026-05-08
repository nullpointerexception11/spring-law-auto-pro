package com.lawauto.backend.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ClientPersonDetail")
@Getter
@Setter
@NoArgsConstructor
public class ClientPersonDetail {
    @Id
    @Column(name = "clientId")
    private UUID clientId;

    @Column(name = "tcKimlikNo", length = 11)
    private String tcKimlikNo;

    @Column(name = "birthDate")
    private LocalDate birthDate;

    private String gender;

    @Column(columnDefinition = "TEXT DEFAULT 'TC'")
    private String nationality = "TC";
}
