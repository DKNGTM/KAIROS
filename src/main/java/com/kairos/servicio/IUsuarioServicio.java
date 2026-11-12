package com.kairos.servicio;

import com.kairos.dto.RegistroUsuarioDTO;
import com.kairos.modelo.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de negocio para la gestión de cuentas de usuario.
 */
public interface IUsuarioServicio {

   
    Usuario registrar(RegistroUsuarioDTO dto);


    Usuario autenticar(String identificador, String contrasena);


    Usuario obtenerPorId(Long id);


    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    List<Usuario> listarUsuarios();


}

