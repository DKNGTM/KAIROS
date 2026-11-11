package com.kairos.excepciones;

/**
 * Se lanza cuando un usuario intenta ejecutar una acción para la que no tiene permisos
 * o cuando una regla de negocio impide la operación.
 */
public class OperacionNoAutorizadaException extends RuntimeException {

    public OperacionNoAutorizadaException(String mensaje) {
        super(mensaje);
    }
}

