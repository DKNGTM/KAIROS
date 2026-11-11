package com.kairos.servicio.impl;

import com.kairos.modelo.Categoria;
import com.kairos.modelo.Subcategoria;
import com.kairos.repositorio.CategoriaRepositorio;
import com.kairos.repositorio.SubcategoriaRepositorio;
import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.servicio.ICategoriaServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaServicioImpl implements ICategoriaServicio {

    private final CategoriaRepositorio categoriaRepositorio;
    private final SubcategoriaRepositorio subcategoriaRepositorio;

    public CategoriaServicioImpl(CategoriaRepositorio categoriaRepositorio,
                                 SubcategoriaRepositorio subcategoriaRepositorio) {
        this.categoriaRepositorio = categoriaRepositorio;
        this.subcategoriaRepositorio = subcategoriaRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepositorio.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria obtenerPorId(Long id) {
        return categoriaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategoria> listarSubcategorias() {
        return subcategoriaRepositorio.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategoria> listarSubcategoriasPorCategoria(Long categoriaId) {
        if (categoriaId == null) {
            return List.of();
        }
        return subcategoriaRepositorio.findByCategoriaIdOrderByNombreAsc(categoriaId);
    }

    @Override
    @Transactional(readOnly = true)
    public Subcategoria obtenerSubcategoriaPorId(Long id) {
        return subcategoriaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Subcategoría no encontrada: " + id));
    }
}

