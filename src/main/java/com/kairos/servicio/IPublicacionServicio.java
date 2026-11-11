package com.kairos.servicio;

import com.kairos.dto.PublicacionFormDTO;
import com.kairos.enums.EstadoPublicacion;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Usuario;

import java.util.List;

/**
 * Contrato de negocio para la publicación, consulta y moderación de ofertas.
 */
public interface IPublicacionServicio {

    /**
     * Devuelve las ofertas visibles en el feed ordenadas de más reciente a más antigua.
     */
    List<Publicacion> obtenerPublicacionesActivas();

    /**
     * Busca ofertas activas aplicando texto libre, categoría, subcategoría y criterio de orden.
     *
     * @param texto
     * @param categoriaId
     * @param subcategoriaId
     * @param orden
     */
    List<Publicacion> buscarPublicaciones(String texto, Long categoriaId, Long subcategoriaId, String orden);

    /**
     * Recupera una publicación por identificador.
     *
     * @throws com.kairos.excepciones.RecursoNoEncontradoException si no existe
     */
    Publicacion obtenerPorId(Long id);


    List<Publicacion> obtenerRelacionadas(Publicacion publicacion, int limite);

    /**
     * Registra una oferta en estado PENDIENTE, almacenando las imágenes recibidas.
     */
    Publicacion guardarPublicacion(PublicacionFormDTO dto, Usuario autor);


    void reportarPublicacion(Long publicacionId, Usuario usuario);


    void cambiarEstado(Long publicacionId, EstadoPublicacion estado);


    void aprobar(Long publicacionId);


    void rechazar(Long publicacionId);


    void marcarVencida(Long publicacionId);


    void inhabilitar(Long publicacionId);


    void reactivar(Long publicacionId);


    void eliminar(Long publicacionId);


    List<Publicacion> listarTodas();

    /** Lista publicaciones filtradas por estado. */
    List<Publicacion> listarPorEstado(EstadoPublicacion estado);

    /** Lista las publicaciones creadas por un usuario. */
    List<Publicacion> listarPorUsuario(Long usuarioId);


    long contarTodas();

    long contarPorEstado(EstadoPublicacion estado);
}

