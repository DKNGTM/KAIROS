package com.kairos.excepciones;

/**
 * Se lanza cuando un usuario intenta ejecutar una acción para la que no tiene permisos
 */
public class OperacionNoAutorizadaException extends RuntimeException {

    public OperacionNoAutorizadaException(String mensaje) {
        super(mensaje);
    }
}

