package com.marisoft.booking.shared;

import lombok.Getter;

@Getter
public enum ResourceType {
    PROFESSIONAL("Profesional"),
    INFRASTRUCTURE("Infraestructura");

    private final String displayName;

    ResourceType(String displayName) {
        this.displayName = displayName;
    }
}