package com.kairos.servicio;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrato de negocio para el almacenamiento físico de imágenes de ofertas.
 */
public interface IAlmacenamientoServicio {

    /**
     * Guarda una imagen validando tipo y tamaño.
     *
     * @return la ruta pública con la que se sirve la imagen (por ejemplo {@code /uploads/uuid.jpg})
     */
    String guardarArchivo(MultipartFile archivo);

    /**
     * Elimina un archivo previamente almacenado a partir de su ruta pública.
     */
    void eliminarArchivo(String rutaPublica);

    /**
     * Recupera un archivo almacenado como recurso servible.
     *
     * @throws com.kairos.excepciones.RecursoNoEncontradoException si el archivo no existe
     */
    Resource cargarComoRecurso(String nombreArchivo);
}

