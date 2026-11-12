package com.kairos.dto;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class PublicacionFormDTO {

    @NotBlank(message = "El título de la oferta es obligatorio.")
    @Size(max = 160, message = "El título no puede superar los 160 caracteres.")
    private String titulo;

    @NotNull(message = "Debes indicar el precio de la oferta.")
    @DecimalMin(value = "0.01", message = "El precio de la oferta debe ser mayor que cero.")
    @Digits(integer = 10, fraction = 2, message = "El precio admite como máximo dos decimales.")
    private BigDecimal precioOferta;

    @DecimalMin(value = "0.00", message = "El precio original no puede ser negativo.")
    @Digits(integer = 10, fraction = 2, message = "El precio admite como máximo dos decimales.")
    private BigDecimal precioOriginal;

    @NotBlank(message = "El enlace a la oferta es obligatorio.")
    @Size(max = 500, message = "El enlace es demasiado largo.")
    @Pattern(regexp = "^https?://.+", message = "El enlace debe comenzar por http:// o https://")
    private String urlOferta;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(max = 4000, message = "La descripción no puede superar los 4000 caracteres.")
    private String descripcion;

    @NotNull(message = "Selecciona una categoría.")
    private Long categoriaId;

    @NotNull(message = "Selecciona una subcategoría.")
    private Long subcategoriaId;

    private List<MultipartFile> imagenes = new ArrayList<>();

    /** Validación cruzada: el precio original no puede ser inferior al precio de oferta. */
    @AssertTrue(message = "El precio original debe ser mayor o igual que el precio de oferta.")
    public boolean isPrecioCoherente() {
        if (precioOferta == null || precioOriginal == null) {
            return true;
        }
        return precioOriginal.compareTo(precioOferta) >= 0;
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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getSubcategoriaId() {
        return subcategoriaId;
    }

    public void setSubcategoriaId(Long subcategoriaId) {
        this.subcategoriaId = subcategoriaId;
    }

    public List<MultipartFile> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<MultipartFile> imagenes) {
        this.imagenes = imagenes;
    }
}

