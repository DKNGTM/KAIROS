package com.kairos.servicio;

import com.kairos.dto.PublicacionFormDTO;
import com.kairos.enums.EstadoPublicacion;
import com.kairos.modelo.Publicacion;
import com.kairos.modelo.Usuario;

import java.util.List;

public interface IPublicacionServicio {


    //Devuelve las ofertas visibles en el feed ordenadas de más reciente a más antigua.
    List<Publicacion> obtenerPublicacionesActivas();


    List<Publicacion> buscarPublicaciones(String texto, Long categoriaId, Long subcategoriaId, String orden);


    Publicacion obtenerPorId(Long id);


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

    
    List<Publicacion> listarPorEstado(EstadoPublicacion estado);

   
    List<Publicacion> listarPorUsuario(Long usuarioId);



  
}

