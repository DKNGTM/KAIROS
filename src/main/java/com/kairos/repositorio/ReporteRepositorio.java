package com.kairos.repositorio;

import com.kairos.modelo.Reporte;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepositorio extends JpaRepository<Reporte, Long> {

    boolean existsByUsuarioIdAndPublicacionId(Long usuarioId, Long publicacionId);

    long countByPublicacionId(Long publicacionId);

    @EntityGraph(attributePaths = {"usuario", "publicacion"})
    List<Reporte> findByPublicacionIdOrderByFechaReporteDesc(Long publicacionId);

    @EntityGraph(attributePaths = {"usuario", "publicacion"})
    List<Reporte> findAllByOrderByFechaReporteDesc();

    void deleteByPublicacionId(Long publicacionId);
}

