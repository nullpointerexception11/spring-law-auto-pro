package com.lawauto.backend.user;

public enum RoleKey {
    PLATFORM_ADMIN,
    ORG_ADMIN,
    LAWYER,
    STAFF,
    CLIENT;

    public boolean isPlatformLevel() {
        return this == PLATFORM_ADMIN;
    }

    public boolean isOrgAdmin() {
        return this == ORG_ADMIN;
    }

    public boolean isInternal() {
        return this == ORG_ADMIN || this == LAWYER || this == STAFF;
    }
}
