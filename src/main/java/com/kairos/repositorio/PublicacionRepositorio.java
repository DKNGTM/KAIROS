package com.kairos.repositorio;

import com.kairos.enums.EstadoPublicacion;
import com.kairos.modelo.Publicacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PublicacionRepositorio extends JpaRepository<Publicacion, Long> {

    @Override
    @EntityGraph(attributePaths = {"usuario", "categoria", "subcategoria"})
    Optional<Publicacion> findById(Long id);

    @EntityGraph(attributePaths = {"usuario", "categoria", "subcategoria"})
    List<Publicacion> findByEstadoOrderByFechaCreacionDesc(EstadoPublicacion estado);

    @EntityGraph(attributePaths = {"usuario", "categoria", "subcategoria"})
    List<Publicacion> findAllByOrderByFechaCreacionDesc();

    @EntityGraph(attributePaths = {"usuario", "categoria", "subcategoria"})
    List<Publicacion> findByEstadoOrderByPrecioOfertaAsc(EstadoPublicacion estado);

    @EntityGraph(attributePaths = {"usuario", "categoria", "subcategoria"})
    List<Publicacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    long countByEstado(EstadoPublicacion estado);

    /**
     * Búsqueda combinada del feed: texto libre + categoría + subcategoría.
     * El orden se aplica después en la capa de servicio.
     */
    @Query("""
            SELECT p FROM Publicacion p
              JOIN FETCH p.usuario
              JOIN FETCH p.categoria c
              JOIN FETCH p.subcategoria s
            WHERE p.estado IN :estados
              AND (:texto IS NULL
                   OR LOWER(p.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :texto, '%')))
              AND (:categoriaId IS NULL OR c.id = :categoriaId)
              AND (:subcategoriaId IS NULL OR s.id = :subcategoriaId)
            """)
    List<Publicacion> buscar(
            @Param("estados") List<EstadoPublicacion> estados,
            @Param("texto") String texto,
            @Param("categoriaId") Long categoriaId,
            @Param("subcategoriaId") Long subcategoriaId
    );
}

