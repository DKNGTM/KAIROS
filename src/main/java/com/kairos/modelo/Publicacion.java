package com.kairos.modelo;

import com.kairos.enums.EstadoPublicacion;
import jakarta.persistence.*;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "publicaciones")
public class Publicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(name = "precio_oferta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioOferta;

    @Column(name = "precio_original", precision = 12, scale = 2)
    private BigDecimal precioOriginal;

    @Column(name = "url_oferta", nullable = false, length = 500)
    private String urlOferta;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Rutas locales de las imágenes, delimitadas por coma. */
    @Column(name = "imagenes_urls", columnDefinition = "TEXT")
    private String imagenesUrls;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPublicacion estado = EstadoPublicacion.PENDIENTE;

    @Column(name = "reportes_count", nullable = false)
    private int reportesCount = 0;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subcategoria_id", nullable = false)
    private Subcategoria subcategoria;


    
    @PrePersist
    public void prepararRegistro() {
        if (reportesCount < 0) {
            reportesCount = 0;
        }
    }

    /** Lista de imágenes ya separadas para las galerías de Thymeleaf. */
    @Transient
    public List<String> getListaImagenes() {
        List<String> imagenes = new ArrayList<>();
        if (imagenesUrls == null || imagenesUrls.isBlank()) {
            return imagenes;
        }
        for (String ruta : imagenesUrls.split(",")) {
            String limpia = ruta.trim();
            if (!limpia.isEmpty()) {
                imagenes.add(limpia);
            }
        }
        return imagenes;
    }

    @Transient
    public String getImagenPrincipal() {
        List<String> imagenes = getListaImagenes();
        return imagenes.isEmpty() ? "/images/placeholder.svg" : imagenes.get(0);
    }







    @Transient
    public boolean isVencida() {
        return estado == EstadoPublicacion.VENCIDA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getPrecioOferta() {
        return precioOferta;
    }

    public void setPrecioOferta(BigDecimal precioOferta) {
        this.precioOferta = precioOferta;
    }

    public BigDecimal getPrecioOriginal() {
        return precioOriginal;
    }

    public void setPrecioOriginal(BigDecimal precioOriginal) {
        this.precioOriginal = precioOriginal;
    }

    public String getUrlOferta() {
        return urlOferta;
    }

    public void setUrlOferta(String urlOferta) {
        this.urlOferta = urlOferta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagenesUrls() {
        return imagenesUrls;
    }

    public void setImagenesUrls(String imagenesUrls) {
        this.imagenesUrls = imagenesUrls;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public int getReportesCount() {
        return reportesCount;
    }

    public void setReportesCount(int reportesCount) {
        this.reportesCount = reportesCount;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Subcategoria getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(Subcategoria subcategoria) {
        this.subcategoria = subcategoria;
    }
}

