package com.kairos.controlador;

import com.kairos.dto.PublicacionFormDTO;
import com.kairos.enums.EstadoPublicacion;
import com.kairos.excepciones.OperacionNoAutorizadaException;
import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Usuario;
import com.kairos.servicio.ICategoriaServicio;
import com.kairos.servicio.IPublicacionServicio;
import com.kairos.servicio.IReporteServicio;
import com.kairos.servicio.PrecioServicio;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de publicaciones: creación de ofertas, detalle y reportes comunitarios.
 */
@Controller
@RequestMapping("/publicacion")
public class PublicacionControlador {

    private final IPublicacionServicio publicacionServicio;
    private final ICategoriaServicio categoriaServicio;
    private final IReporteServicio reporteServicio;
    private final PrecioServicio precioServicio;

    public PublicacionControlador(IPublicacionServicio publicacionServicio,
                                   ICategoriaServicio categoriaServicio,
                                   IReporteServicio reporteServicio,
                                    PrecioServicio precioServicio) {
        this.publicacionServicio = publicacionServicio;
        this.categoriaServicio = categoriaServicio;
        this.reporteServicio = reporteServicio;
        this.precioServicio = precioServicio;
    }

    @GetMapping("/crear")
    public String mostrarFormulario(HttpSession session, Model model) {
        if (usuarioSesion(session) == null) {
            return "redirect:/auth/login";
        }
        if (!model.containsAttribute("publicacionForm")) {
            model.addAttribute("publicacionForm", new PublicacionFormDTO());
        }
        cargarTaxonomia(model);
        return "publicacion/crear";
    }

    @PostMapping("/crear")
    public String crearPublicacion(@Valid @ModelAttribute("publicacionForm") PublicacionFormDTO formulario,
                                   BindingResult resultado,
                                   HttpSession session,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Usuario autor = usuarioSesion(session);
        if (autor == null) {
            return "redirect:/auth/login";
        }

        if (resultado.hasErrors()) {
            cargarTaxonomia(model);
            return "publicacion/crear";
        }

        try {
            Publicacion publicacion = publicacionServicio.guardarPublicacion(formulario, autor);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Oferta enviada a moderación. Un administrador la revisará en breve.");
            return "redirect:/publicacion/" + publicacion.getId();
        } catch (OperacionNoAutorizadaException | IllegalArgumentException ex) {
            resultado.reject("errorPublicacion", ex.getMessage());
            cargarTaxonomia(model);
            return "publicacion/crear";
        }
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        Publicacion publicacion;
        try {
            publicacion = publicacionServicio.obtenerPorId(id);
        } catch (RecursoNoEncontradoException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/feed";
        }

        Usuario usuario = usuarioSesion(session);
        boolean esAdmin = Boolean.TRUE.equals(session.getAttribute("esAdmin"));
        boolean esAutor = usuario != null && publicacion.getUsuario() != null
                && usuario.getId().equals(publicacion.getUsuario().getId());

        // 1. Las ofertas pendientes, rechazadas o inhabilitadas solo son visibles para su autor o un admin.
        boolean visible = publicacion.getEstado() == EstadoPublicacion.ACTIVA
                || publicacion.getEstado() == EstadoPublicacion.VENCIDA;
        if (!visible && !esAutor && !esAdmin) {
            redirectAttributes.addFlashAttribute("error",
                    "Esta publicación no está disponible en el feed público.");
            return "redirect:/feed";
        }

        boolean reportado = usuario != null
                && reporteServicio.yaReporto(publicacion.getId(), usuario.getId());

        model.addAttribute("publicacion", publicacion);

        model.addAttribute("esAutor", esAutor);
        model.addAttribute("esAdmin", esAdmin);
        model.addAttribute("reportado", reportado);
        model.addAttribute("puedeReportar", usuario != null && !reportado
                && publicacion.getEstado() == EstadoPublicacion.ACTIVA);


        model.addAttribute("ahorro", precioServicio.calcularAhorro(publicacion));
        model.addAttribute("porcentajeDescuento",
                precioServicio.calcularPorcentajeDescuento(publicacion));

        return "publicacion/detalle";
    }

    @PostMapping("/{id}/reportar")
    public String reportar(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioSesion(session);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error",
                    "Debes iniciar sesión para reportar una oferta.");
            return "redirect:/auth/login";
        }

        try {
            reporteServicio.registrarReporte(id, usuario);
            Publicacion publicacion = publicacionServicio.obtenerPorId(id);
            redirectAttributes.addFlashAttribute("mensaje",
                    "Reporte registrado (" + publicacion.getReportesCount()
                            + "/10). Gracias por cuidar la integridad del feed.");
        } catch (OperacionNoAutorizadaException | RecursoNoEncontradoException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        return "redirect:/publicacion/" + id;
    }

    private void cargarTaxonomia(Model model) {
        model.addAttribute("categorias", categoriaServicio.listarCategorias());
        model.addAttribute("subcategorias", categoriaServicio.listarSubcategorias());
    }

    private Usuario usuarioSesion(HttpSession session) {
        Object valor = session.getAttribute("usuarioLogueado");
        return valor instanceof Usuario usuario ? usuario : null;
    }
}

