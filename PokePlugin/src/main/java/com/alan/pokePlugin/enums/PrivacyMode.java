package com.alan.pokePlugin.enums;

public enum PrivacyMode {
    ALLOW_ALL("Allow All"),
    DISABLED("Disabled"),
    CUSTOM("Custom");

    private final String displayName;

    PrivacyMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PrivacyMode fromString(String mode) {
        try {
            return PrivacyMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ALLOW_ALL;
        }
    }
}