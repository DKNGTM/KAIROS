package com.kairos.servicio;

import com.kairos.modelo.Categoria;
import com.kairos.modelo.Subcategoria;

import java.util.List;


public interface ICategoriaServicio {

    List<Categoria> listarCategorias();


    Categoria obtenerPorId(Long id);

    //muestra las subcategorias dependiendo de la categoria
    List<Subcategoria> listarSubcategoriasPorCategoria(Long categoriaId);

     List<Subcategoria> listarSubcategorias();

    Subcategoria obtenerSubcategoriaPorId(Long id);


}

