package com.kairos.servicio;

import com.kairos.modelo.Categoria;
import com.kairos.modelo.Subcategoria;

import java.util.List;

/**
 * Contrato de negocio para la taxonomía de categorías y subcategorías.
 */
public interface ICategoriaServicio {

    /** Devuelve todas las categorías principales. */
    List<Categoria> listarCategorias();

    /**
     * Recupera una categoría por identificador.
     *
     * @throws com.kairos.excepciones.RecursoNoEncontradoException si no existe
     */
    Categoria obtenerPorId(Long id);

    /** Devuelve todas las subcategorías. */
    List<Subcategoria> listarSubcategorias();

    /** Devuelve las subcategorías pertenecientes a una categoría. */
    List<Subcategoria> listarSubcategoriasPorCategoria(Long categoriaId);

    /**
     * Recupera una subcategoría por identificador.
     *
     * @throws com.kairos.excepciones.RecursoNoEncontradoException si no existe
     */
    Subcategoria obtenerSubcategoriaPorId(Long id);
}

