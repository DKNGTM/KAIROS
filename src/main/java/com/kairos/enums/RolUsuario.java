package com.kairos.enums;

/**
 * Roles disponibles.
 */
public enum RolUsuario {

    /** Usuario administrador*/
    ADMIN("Administrador"),

    /** Usuario estándar */
    USER("Usuario");

    private final String etiqueta;

    RolUsuario(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

