package com.uqtr.decodeurtr.entity;

public enum EtatDecodeur {
    EN_LIGNE,
    HORS_LIGNE;

    public static EtatDecodeur fromSimulateurState(String state) {
        if (state == null) {
            return HORS_LIGNE;
        }

        return switch (state.trim().toLowerCase()) {
            case "active" -> EN_LIGNE;
            case "inactive" -> HORS_LIGNE;
            default -> HORS_LIGNE;
        };
    }

    public String getLabel() {
        return this == EN_LIGNE ? "En ligne" : "Hors ligne";
    }

    public boolean isEnLigne() {
        return this == EN_LIGNE;
    }
}