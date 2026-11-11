package com.kairos.modelo;

import jakarta.persistence.*;

/**
 * Subcategoría dependiente de una {@link Categoria} principal.
 */
@Entity
@Table(
        name = "subcategorias",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_subcategoria_nombre_categoria",
                columnNames = {"nombre", "categoria_id"}
        )
)
public class Subcategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Subcategoria() {
    }

    public Subcategoria(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}

