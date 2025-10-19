package com.marisoft.booking.shared;

import lombok.Getter;

@Getter
public enum Role {
    ROLE_ADMIN("Administrador"),
    ROLE_STAFF("Personal");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

}
