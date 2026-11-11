package com.kairos.servicio;

import com.kairos.modelo.Reporte;
import com.kairos.modelo.Usuario;

import java.util.List;

/**
 * Contrato de negocio para los reportes comunitarios (RF005).
 */
public interface IReporteServicio {

    /**
     * Registra un reporte único de un usuario sobre una publicación.
     * Incrementa el contador y, al llegar a 10, inhabilita la oferta.
     *
     * @throws com.kairos.excepciones.OperacionNoAutorizadaException si el usuario ya reportó la oferta
     */
    Reporte registrarReporte(Long publicacionId, Usuario usuario);

    /** Indica si un usuario ya reportó una publicación. */
    boolean yaReporto(Long publicacionId, Long usuarioId);

    /** Cuenta los reportes acumulados de una publicación. */
    long contarReportes(Long publicacionId);

    /** Lista todos los reportes, del más reciente al más antiguo. */
    List<Reporte> listarTodos();

    /** Lista los reportes de una publicación concreta. */
    List<Reporte> listarPorPublicacion(Long publicacionId);

    /** Elimina los reportes asociados a una publicación antes de un borrado definitivo. */
    void eliminarReportesDePublicacion(Long publicacionId);
}

