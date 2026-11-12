package com.kairos.enums;


public enum EstadoPublicacion {

    PENDIENTE("Pendiente", "warning"),
    ACTIVA("Activa", "success"),
    RECHAZADA("Rechazada", "danger"),
    INACTIVA("Inhabilitada", "danger"),
    VENCIDA("Vencida", "muted");

    private final String etiqueta;
    private final String tono;

    EstadoPublicacion(String etiqueta, String tono) {
        this.etiqueta = etiqueta;
        this.tono = tono;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getTono() {
        return tono;
    }

    //valida si la publicación puede aparecer en el feed principal
    public boolean esVisibleEnFeed() {
        return this == ACTIVA || this == VENCIDA;
    }
}

