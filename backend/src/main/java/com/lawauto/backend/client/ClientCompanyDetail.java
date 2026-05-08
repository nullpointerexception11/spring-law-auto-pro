package com.lawauto.backend.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "ClientCompanyDetail")
@Getter
@Setter
@NoArgsConstructor
public class ClientCompanyDetail {
    @Id
    @Column(name = "clientId")
    private UUID clientId;

    @Column(name = "taxNo", length = 20)
    private String taxNo;

    @Column(name = "taxOffice")
    private String taxOffice;

    @Column(name = "mersisNo", length = 20)
    private String mersisNo;

    @Column(name = "tradeRegistryNo")
    private String tradeRegistryNo;
}
