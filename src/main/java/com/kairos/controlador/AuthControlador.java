package com.kairos.controlador;

import com.kairos.dto.RegistroUsuarioDTO;
import com.kairos.enums.RolUsuario;
import com.kairos.excepciones.OperacionNoAutorizadaException;
import com.kairos.modelo.Usuario;
import com.kairos.servicio.IUsuarioServicio;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/auth")
public class AuthControlador {

    private final IUsuarioServicio usuarioServicio;

    public AuthControlador(IUsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    @GetMapping("/login")
    public String mostrarLogin(HttpSession session, Model model) {

        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/feed";
        }
        if (!model.containsAttribute("identificador")) {
            model.addAttribute("identificador", "");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String identificador,
                                @RequestParam String contrasena,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            
            Usuario usuario = usuarioServicio.autenticar(identificador, contrasena);

            
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("esAdmin", usuario.getRol() == RolUsuario.ADMIN);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Sesión iniciada como @" + usuario.getNombreUsuario() + ".");
            return "redirect:/feed";
        } catch (OperacionNoAutorizadaException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("identificador", identificador);
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/registro")
    public String mostrarRegistro(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/feed";
        }
        if (!model.containsAttribute("registro")) {
            model.addAttribute("registro", new RegistroUsuarioDTO());
        }
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registro") RegistroUsuarioDTO registro,
                            BindingResult resultado,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (resultado.hasErrors()) {
            return "auth/registro";
        }

        try {

            Usuario usuario = usuarioServicio.registrar(registro);
            session.setAttribute("usuarioLogueado", usuario);
            session.setAttribute("esAdmin", usuario.getRol() == RolUsuario.ADMIN);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Cuenta creada. Bienvenido a Kairos, @" + usuario.getNombreUsuario() + ".");
            return "redirect:/feed";
            
        } catch (OperacionNoAutorizadaException ex) {
            resultado.reject("registroError", ex.getMessage());
            return "auth/registro";
        }
    }

    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "Sesión cerrada correctamente.");
        return "redirect:/feed";
    }
}

