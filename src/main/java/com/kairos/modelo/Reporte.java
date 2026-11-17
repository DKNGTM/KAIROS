package com.kairos.modelo;

import jakarta.persistence.*;


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

    public Reporte() {
    }

    public Reporte(Usuario usuario, Publicacion publicacion) {
        this.usuario = usuario;
        this.publicacion = publicacion;
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


}

