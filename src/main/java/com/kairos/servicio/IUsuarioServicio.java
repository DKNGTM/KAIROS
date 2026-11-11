package com.kairos.servicio;

import com.kairos.dto.RegistroUsuarioDTO;
import com.kairos.modelo.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de negocio para la gestión de cuentas de usuario.
 */
public interface IUsuarioServicio {

    /**
     * Registra un nuevo usuario estándar validando la unicidad de nombre y correo.
     *
     * @param dto datos del formulario de registro
     * @return el usuario persistido
     * @throws com.kairos.excepciones.OperacionNoAutorizadaException si el nombre o el correo ya existen
     */
    Usuario registrar(RegistroUsuarioDTO dto);

    /**
     * Autentica a un usuario mediante su nombre de usuario o su correo electrónico.
     *
     * @return el usuario autenticado
     * @throws com.kairos.excepciones.OperacionNoAutorizadaException si las credenciales no coinciden
     */
    Usuario autenticar(String identificador, String contrasena);

    /**
     * Recupera un usuario por su identificador.
     *
     * @throws com.kairos.excepciones.RecursoNoEncontradoException si no existe
     */
    Usuario obtenerPorId(Long id);

    /**
     * Busca un usuario por su nombre de usuario sin lanzar excepción.
     */
    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);

    /**
     * Devuelve todos los usuarios registrados ordenados por identificador.
     */
    List<Usuario> listarUsuarios();

    /**
     * Cuenta el número total de usuarios registrados.
     */
    long contarUsuarios();
}

