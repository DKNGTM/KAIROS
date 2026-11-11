package com.kairos.modelo;

import com.kairos.enums.RolUsuario;
import jakarta.persistence.*;

/**
 * Entidad que representa a una persona registrada en Kairos.
 */
@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_nombre", columnNames = "nombre_usuario"),
                @UniqueConstraint(name = "uk_usuario_correo", columnNames = "correo")
        }
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", nullable = false, length = 40)
    private String nombreUsuario;

    @Column(nullable = false, length = 120)
    private String correo;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol = RolUsuario.USER;

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String correo, String contrasena, RolUsuario rol) {
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    /** Iniciales usadas por los avatares de la interfaz. */
    @Transient
    public String getIniciales() {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            return "??";
        }
        String limpio = nombreUsuario.replace("@", "").trim();
        return limpio.length() >= 2
                ? limpio.substring(0, 2).toUpperCase()
                : limpio.substring(0, 1).toUpperCase();
    }

    @Transient
    public boolean isAdmin() {
        return rol == RolUsuario.ADMIN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}

