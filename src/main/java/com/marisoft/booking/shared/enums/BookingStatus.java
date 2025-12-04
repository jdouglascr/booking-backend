package com.marisoft.booking.shared.enums;

import lombok.Getter;

@Getter
public enum BookingStatus {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    PAGADA("Pagada"),
    COMPLETADA("Completada"),
    CANCELADA("Cancelada");

    private final String displayName;

    BookingStatus(String displayName) {
        this.displayName = displayName;
    }

    public static BookingStatus fromString(String status) {
        for (BookingStatus bs : BookingStatus.values()) {
            if (bs.displayName.equalsIgnoreCase(status)) {
                return bs;
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + status);
    }
}