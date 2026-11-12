package com.kairos.controlador;


import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.modelo.Categoria;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Subcategoria;
import com.kairos.modelo.Usuario;
import com.kairos.servicio.ICategoriaServicio;
import com.kairos.servicio.IPublicacionServicio;
import com.kairos.servicio.IReporteServicio;
import com.kairos.servicio.IUsuarioServicio;
import com.kairos.servicio.PrecioServicio;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class FeedControlador {

    private final IPublicacionServicio publicacionServicio;
    private final ICategoriaServicio categoriaServicio;
    private final IUsuarioServicio usuarioServicio;
    private final PrecioServicio precioServicio;

    public FeedControlador(IPublicacionServicio publicacionServicio,
                           ICategoriaServicio categoriaServicio,
                           IReporteServicio reporteServicio,
                           IUsuarioServicio usuarioServicio,
                            PrecioServicio precioServicio) {
        this.publicacionServicio = publicacionServicio;
        this.categoriaServicio = categoriaServicio;
        this.usuarioServicio = usuarioServicio;
        this.precioServicio = precioServicio;
    }

    @GetMapping({"/", "/feed"})
    public String verFeed(@RequestParam(required = false) String q,
                          @RequestParam(required = false) Long categoria,
                          @RequestParam(required = false) Long subcategoria,
                          @RequestParam(required = false, defaultValue = "recientes") String orden,
                          Model model,
                          HttpSession session) {

        List<Publicacion> publicaciones = publicacionServicio.buscarPublicaciones(q, categoria, subcategoria, orden);

        List<Categoria> categorias = categoriaServicio.listarCategorias();


        List<Subcategoria> subcategorias = categoria == null
                ? categoriaServicio.listarSubcategorias()
                : categoriaServicio.listarSubcategoriasPorCategoria(categoria);

                
        Categoria categoriaActual = null;
        if (categoria != null) {
            try {
                categoriaActual = categoriaServicio.obtenerPorId(categoria);
            } catch (RecursoNoEncontradoException ignored) {

            }
        }

        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("categorias", categorias);
        model.addAttribute("subcategorias", subcategorias);
        model.addAttribute("categoriaActual", categoriaActual);
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("subcategoriaSeleccionada", subcategoria);
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("orden", orden);


        model.addAttribute("ahorros", precioServicio.calcularAhorros(publicaciones));
        model.addAttribute("porcentajesDescuento",
                precioServicio.calcularPorcentajesDescuento(publicaciones));


        return "publicacion/feed";
    }

    private Usuario usuarioSesion(HttpSession session) {
        Object valor = session.getAttribute("usuarioLogueado");
        return valor instanceof Usuario usuario ? usuario : null;
    }
}

