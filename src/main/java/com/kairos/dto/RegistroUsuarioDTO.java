package com.kairos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para el registro de nuevos usuarios.
 */
public class RegistroUsuarioDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 40, message = "El nombre de usuario debe tener entre 3 y 40 caracteres.")
    @Pattern(
            regexp = "^[a-zA-Z0-9_.-]+$",
            message = "Solo se permiten letras, números, punto, guion y guion bajo."
    )
    private String nombreUsuario;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Introduce un correo válido.")
    @Size(max = 120, message = "El correo es demasiado largo.")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 8, max = 80, message = "La contraseña debe tener al menos 8 caracteres.")
    private String contrasena;

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}

