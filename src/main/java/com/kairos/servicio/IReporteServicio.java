package com.kairos.servicio;

import com.kairos.modelo.Reporte;
import com.kairos.modelo.Usuario;

import java.util.List;

/**
 * Contrato de negocio para los reportes comunitarios (RF005).
 */
public interface IReporteServicio {


    Reporte registrarReporte(Long publicacionId, Usuario usuario);

    //Indica si un usuario ya reportó una publicación
    boolean yaReporto(Long publicacionId, Long usuarioId);

   
    long contarReportes(Long publicacionId);

   
    List<Reporte> listarTodos();

  
    List<Reporte> listarPorPublicacion(Long publicacionId);

    void eliminarReportesDePublicacion(Long publicacionId);
}

