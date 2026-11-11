package com.kairos.repositorio;

import com.kairos.modelo.Subcategoria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubcategoriaRepositorio extends JpaRepository<Subcategoria, Long> {

    @EntityGraph(attributePaths = "categoria")
    List<Subcategoria> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "categoria")
    List<Subcategoria> findByCategoriaIdOrderByNombreAsc(Long categoriaId);

    Optional<Subcategoria> findByNombreIgnoreCaseAndCategoriaId(String nombre, Long categoriaId);

    boolean existsByNombreIgnoreCaseAndCategoriaId(String nombre, Long categoriaId);
}

