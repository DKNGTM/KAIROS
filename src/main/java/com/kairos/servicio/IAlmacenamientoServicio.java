package com.kairos.servicio;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrato de negocio para el almacenamiento físico de imágenes de ofertas.
 */
public interface IAlmacenamientoServicio {

  
    String guardarArchivo(MultipartFile archivo);

    void eliminarArchivo(String rutaPublica);


    Resource cargarComoRecurso(String nombreArchivo);
}

