package com.kairos.servicio.impl;

import com.kairos.enums.EstadoPublicacion;
import com.kairos.excepciones.OperacionNoAutorizadaException;
import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Reporte;
import com.kairos.modelo.Usuario;
import com.kairos.repositorio.PublicacionRepositorio;
import com.kairos.repositorio.ReporteRepositorio;
import com.kairos.servicio.IReporteServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReporteServicioImpl implements IReporteServicio {

    private static final int UMBRAL_REPORTES = 10;

    private final ReporteRepositorio reporteRepositorio;
    private final PublicacionRepositorio publicacionRepositorio;

    public ReporteServicioImpl(ReporteRepositorio reporteRepositorio,
                               PublicacionRepositorio publicacionRepositorio) {
        this.reporteRepositorio = reporteRepositorio;
        this.publicacionRepositorio = publicacionRepositorio;
    }

    @Override
    @Transactional
    public Reporte registrarReporte(Long publicacionId, Usuario usuario) {
        if (usuario == null) {
            throw new OperacionNoAutorizadaException("Debes iniciar sesión para reportar una oferta.");
        }

        Publicacion publicacion = publicacionRepositorio.findById(publicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Publicación no encontrada: " + publicacionId));

        //revisa si el usuario ya reporto la publicacion
        if (reporteRepositorio.existsByUsuarioIdAndPublicacionId(usuario.getId(), publicacionId)) {
            throw new OperacionNoAutorizadaException("Ya has reportado esta oferta anteriormente.");
        }

        //Persistir el reporte y sincronizar el contador denormalizado de la publicación.
        Reporte reporte = reporteRepositorio.save(new Reporte(usuario, publicacion));
        publicacion.setReportesCount(publicacion.getReportesCount() + 1);

        //Al alcanzar el maximo de reportes la publicacion desaparece
        if (publicacion.getReportesCount() >= UMBRAL_REPORTES
                && publicacion.getEstado() == EstadoPublicacion.ACTIVA) {
            publicacion.setEstado(EstadoPublicacion.INACTIVA);
        }
        publicacionRepositorio.save(publicacion);
        return reporte;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean yaReporto(Long publicacionId, Long usuarioId) {
        if (publicacionId == null || usuarioId == null) {
            return false;
        }
        return reporteRepositorio.existsByUsuarioIdAndPublicacionId(usuarioId, publicacionId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarReportes(Long publicacionId) {
        return reporteRepositorio.countByPublicacionId(publicacionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> listarTodos() {
        return reporteRepositorio.findAllByOrderByFechaReporteDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> listarPorPublicacion(Long publicacionId) {
        return reporteRepositorio.findByPublicacionIdOrderByFechaReporteDesc(publicacionId);
    }

    @Override
    @Transactional
    public void eliminarReportesDePublicacion(Long publicacionId) {
        reporteRepositorio.deleteByPublicacionId(publicacionId);
    }
}

