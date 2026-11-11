package com.kairos.servicio.impl;

import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.servicio.IAlmacenamientoServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class AlmacenamientoServicioImpl implements IAlmacenamientoServicio {

    /** Prefijo público con el que WebConfig expone el directorio de subidas. */
    public static final String PREFIJO_PUBLICO = "/uploads/";

    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of("jpg", "jpeg", "png", "webp", "gif", "avif");

    private final Path directorioSubidas;

    public AlmacenamientoServicioImpl(
            @Value("${kairos.uploads.dir:src/main/resources/static/images/uploads}") String directorio) {
        this.directorioSubidas = Paths.get(directorio).toAbsolutePath().normalize();
    }

    @Override
    public String guardarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        // 1. Validar tamaño máximo y tipo MIME de imagen.
        if (archivo.getSize() > TAMANO_MAXIMO) {
            throw new IllegalArgumentException("Cada imagen debe pesar como máximo 5 MB.");
        }
        String tipo = archivo.getContentType();
        if (tipo == null || !tipo.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen válida (JPG, PNG o WebP).");
        }

        // 2. Normalizar y validar la extensión del nombre original.
        String extension = extensionDe(archivo.getOriginalFilename());
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IllegalArgumentException("Formato de imagen no permitido: " + extension);
        }

        // 3. Guardar con un nombre único para evitar colisiones.
        String nombre = UUID.randomUUID().toString().replace("-", "") + "." + extension;
        try {
            Files.createDirectories(directorioSubidas);
            Path destino = directorioSubidas.resolve(nombre).normalize();
            if (!destino.startsWith(directorioSubidas)) {
                throw new IllegalArgumentException("Ruta de archivo no válida.");
            }
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo almacenar la imagen: " + ex.getMessage(), ex);
        }

        return PREFIJO_PUBLICO + nombre;
    }

    @Override
    public void eliminarArchivo(String rutaPublica) {
        if (rutaPublica == null || rutaPublica.isBlank()) {
            return;
        }
        String nombre = rutaPublica.startsWith(PREFIJO_PUBLICO)
                ? rutaPublica.substring(PREFIJO_PUBLICO.length())
                : rutaPublica;
        Path archivo = directorioSubidas.resolve(nombre).normalize();
        if (!archivo.startsWith(directorioSubidas)) {
            return;
        }
        try {
            Files.deleteIfExists(archivo);
        } catch (IOException ignored) {
            // La oferta se elimina igualmente; un archivo huérfano no debe bloquear el borrado.
        }
    }

    @Override
    public Resource cargarComoRecurso(String nombreArchivo) {
        try {
            Path archivo = directorioSubidas.resolve(nombreArchivo).normalize();
            if (!archivo.startsWith(directorioSubidas)) {
                throw new RecursoNoEncontradoException("Archivo no válido: " + nombreArchivo);
            }
            Resource recurso = new UrlResource(archivo.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new RecursoNoEncontradoException("Archivo no encontrado: " + nombreArchivo);
            }
            return recurso;
        } catch (IOException ex) {
            throw new RecursoNoEncontradoException("Archivo no encontrado: " + nombreArchivo, ex);
        }
    }

    private String extensionDe(String nombreOriginal) {
        if (nombreOriginal == null) {
            return "";
        }
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto < 0 || punto == nombreOriginal.length() - 1) {
            return "";
        }
        return nombreOriginal.substring(punto + 1).toLowerCase(Locale.ROOT);
    }
}

