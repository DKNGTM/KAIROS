package com.kairos.controlador;

import com.kairos.enums.EstadoPublicacion;
import com.kairos.modelo.Publicacion;
import com.kairos.servicio.IPublicacionServicio;
import com.kairos.servicio.IReporteServicio;
import com.kairos.servicio.IUsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminControlador {

    private final IPublicacionServicio publicacionServicio;
    private final IReporteServicio reporteServicio;
    private final IUsuarioServicio usuarioServicio;

    public AdminControlador(IPublicacionServicio publicacionServicio,
                            IReporteServicio reporteServicio,
                            IUsuarioServicio usuarioServicio) {
        this.publicacionServicio = publicacionServicio;
        this.reporteServicio = reporteServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping({"", "/"})
    public String mostrarPanel(@RequestParam(required = false) String estado,
                               HttpSession session,
                               Model model) {
        if (!esAdmin(session)) {
            return "redirect:/feed";
        }

        EstadoPublicacion filtro = leerEstado(estado);
        List<Publicacion> publicaciones = filtro == null
                ? publicacionServicio.listarTodas()
                : publicacionServicio.listarPorEstado(filtro);

        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("estadoSeleccionado", filtro == null ? "TODAS" : filtro.name());
        model.addAttribute("estados", Arrays.asList(EstadoPublicacion.values()));

    

        model.addAttribute("reportes", reporteServicio.listarTodos());
        model.addAttribute("usuarios", usuarioServicio.listarUsuarios());

        return "admin/panel";
    }




    @PostMapping("/publicacion/{id}/aprobar")
    public String aprobar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/feed";
        }
        return ejecutar(redirectAttributes,
                () -> publicacionServicio.aprobar(id),
                "Oferta aprobada y publicada en el feed.");
    }





    @PostMapping("/publicacion/{id}/rechazar")
    public String rechazar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/feed";
        }
        return ejecutar(redirectAttributes,
                () -> publicacionServicio.rechazar(id),
                "Oferta rechazada por moderación.");
    }




    @PostMapping("/publicacion/{id}/vencer")
    public String marcarVencida(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/feed";
        }
        return ejecutar(redirectAttributes,
                () -> publicacionServicio.marcarVencida(id),
                "Oferta marcada como VENCIDA / AGOTADA (RF004).");
    }




    @PostMapping("/publicacion/{id}/inhabilitar")
    public String inhabilitar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/feed";
        }
        return ejecutar(redirectAttributes,
                () -> publicacionServicio.inhabilitar(id),
                "Oferta inhabilitada y retirada del feed.");
    }



    @PostMapping("/publicacion/{id}/reactivar")
    public String reactivar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/feed";
        }
        return ejecutar(redirectAttributes,
                () -> publicacionServicio.reactivar(id),
                "Oferta reactivada y contador de reportes reiniciado.");
    }



    @PostMapping("/publicacion/{id}/eliminar")
    public String eliminar(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!esAdmin(session)) {
            return "redirect:/feed";
        }
        return ejecutar(redirectAttributes,
                () -> publicacionServicio.eliminar(id),
                "Publicación eliminada definitivamente (RF002).");
    }

    private String ejecutar(RedirectAttributes redirectAttributes, Runnable accion, String mensaje) {
        try {
            accion.run();
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin";
    }

    private boolean esAdmin(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("esAdmin"));
    }


    
    private EstadoPublicacion leerEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        try {
            return EstadoPublicacion.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}

