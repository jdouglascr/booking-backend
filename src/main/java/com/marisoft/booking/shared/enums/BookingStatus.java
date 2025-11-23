package com.marisoft.booking.shared.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BookingStatus {
    PENDIENTE("PENDIENTE"),
    CONFIRMADA("CONFIRMADA"),
    PAGADA("PAGADA"),
    COMPLETADA("COMPLETADA"),
    CANCELADA("CANCELADA");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    public static BookingStatus fromString(String status) {
        for (BookingStatus bs : BookingStatus.values()) {
            if (bs.displayName.equalsIgnoreCase(status)) {
                return bs;
            }
        }
        throw new IllegalArgumentException("Estado inválido: " + status);
    }

    public static boolean isValid(String status) {
        try {
            fromString(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}