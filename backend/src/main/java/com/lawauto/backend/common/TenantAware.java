package com.lawauto.backend.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.util.UUID;

/**
 * Base class for all entities that must be isolated by organization.
 * Uses Hibernate Filter for row‑level security.
 */
@MappedSuperclass
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "orgId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "org_id = :orgId")
@Getter
@Setter
public abstract class TenantAware {

    @Column(name = "org_id", nullable = false, updatable = false)
    private UUID orgId;
}
