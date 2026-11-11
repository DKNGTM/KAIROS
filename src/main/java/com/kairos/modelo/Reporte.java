package com.kairos.modelo;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Reporte comunitario sobre una oferta
 *
 */
@Entity
@Table(
        name = "reportes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_reporte_usuario_publicacion",
                columnNames = {"usuario_id", "publicacion_id"}
        )
)
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "publicacion_id", nullable = false)
    private Publicacion publicacion;

    @Column(name = "fecha_reporte", nullable = false)
    private LocalDateTime fechaReporte;

    public Reporte() {
    }

    public Reporte(Usuario usuario, Publicacion publicacion) {
        this.usuario = usuario;
        this.publicacion = publicacion;
        this.fechaReporte = LocalDateTime.now();
    }

    @PrePersist
    public void prepararRegistro() {
        if (fechaReporte == null) {
            fechaReporte = LocalDateTime.now();
        }
    }

    /** Fecha legible para el panel de administración. */
    @Transient
    public String getFechaFormateada() {
        return fechaReporte == null
                ? "—"
                : fechaReporte.format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm"));
    }

    /** Tiempo transcurrido desde el reporte. */
    @Transient
    public String getTiempoTranscurrido() {
        if (fechaReporte == null) {
            return "AHORA";
        }
        long minutos = Math.max(Duration.between(fechaReporte, LocalDateTime.now()).toMinutes(), 0);
        if (minutos < 60) {
            return "Hace " + minutos + " min";
        }
        long horas = minutos / 60;
        if (horas < 24) {
            return "Hace " + horas + "h";
        }
        return "Hace " + (horas / 24) + "d";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }

    public LocalDateTime getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(LocalDateTime fechaReporte) {
        this.fechaReporte = fechaReporte;
    }
}

