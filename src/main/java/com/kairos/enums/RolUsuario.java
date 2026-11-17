package com.kairos.enums;


public enum RolUsuario {

    ADMIN("Administrador"),


    USER("Usuario");

    private final String etiqueta;

    RolUsuario(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

