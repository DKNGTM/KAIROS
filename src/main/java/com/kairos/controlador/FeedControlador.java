package com.kairos.controlador;

import com.kairos.enums.EstadoPublicacion;
import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.modelo.Categoria;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Subcategoria;
import com.kairos.modelo.Usuario;
import com.kairos.servicio.ICategoriaServicio;
import com.kairos.servicio.IPublicacionServicio;
import com.kairos.servicio.IReporteServicio;
import com.kairos.servicio.IUsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Controlador de la pantalla principal: feed, búsqueda global y filtros por taxonomía.
 */
@Controller
public class FeedControlador {

    private final IPublicacionServicio publicacionServicio;
    private final ICategoriaServicio categoriaServicio;
    private final IReporteServicio reporteServicio;
    private final IUsuarioServicio usuarioServicio;

    public FeedControlador(IPublicacionServicio publicacionServicio,
                           ICategoriaServicio categoriaServicio,
                           IReporteServicio reporteServicio,
                           IUsuarioServicio usuarioServicio) {
        this.publicacionServicio = publicacionServicio;
        this.categoriaServicio = categoriaServicio;
        this.reporteServicio = reporteServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping({"/", "/feed"})
    public String verFeed(@RequestParam(required = false) String q,
                          @RequestParam(required = false) Long categoria,
                          @RequestParam(required = false) Long subcategoria,
                          @RequestParam(required = false, defaultValue = "recientes") String orden,
                          Model model,
                          HttpSession session) {

        List<Publicacion> publicaciones =
                publicacionServicio.buscarPublicaciones(q, categoria, subcategoria, orden);

        List<Categoria> categorias = categoriaServicio.listarCategorias();
        List<Subcategoria> subcategorias = categoria == null
                ? categoriaServicio.listarSubcategorias()
                : categoriaServicio.listarSubcategoriasPorCategoria(categoria);

        Set<Long> reportados = new HashSet<>();
        Usuario usuario = usuarioSesion(session);
        if (usuario != null) {
            for (Publicacion publicacion : publicaciones) {
                if (reporteServicio.yaReporto(publicacion.getId(), usuario.getId())) {
                    reportados.add(publicacion.getId());
                }
            }
        }

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
        model.addAttribute("reportados", reportados);
        model.addAttribute("totalActivas", publicacionServicio.contarPorEstado(EstadoPublicacion.ACTIVA));
        model.addAttribute("totalPendientes", publicacionServicio.contarPorEstado(EstadoPublicacion.PENDIENTE));
        model.addAttribute("totalPublicaciones", publicacionServicio.contarTodas());
        model.addAttribute("totalUsuarios", usuarioServicio.contarUsuarios());

        return "publicacion/feed";
    }

    private Usuario usuarioSesion(HttpSession session) {
        Object valor = session.getAttribute("usuarioLogueado");
        return valor instanceof Usuario usuario ? usuario : null;
    }
}

