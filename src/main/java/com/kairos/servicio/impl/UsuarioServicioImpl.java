package com.kairos.servicio.impl;

import com.kairos.dto.RegistroUsuarioDTO;
import com.kairos.enums.RolUsuario;
import com.kairos.excepciones.OperacionNoAutorizadaException;
import com.kairos.excepciones.RecursoNoEncontradoException;
import com.kairos.modelo.Usuario;
import com.kairos.repositorio.UsuarioRepositorio;
import com.kairos.servicio.IUsuarioServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServicioImpl implements IUsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioServicioImpl(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional
    public Usuario registrar(RegistroUsuarioDTO dto) {
        String nombre = dto.getNombreUsuario().trim();
        String correo = dto.getCorreo().trim().toLowerCase();

        // valida que el nombre y el correo sean unicos
        if (usuarioRepositorio.existsByNombreUsuarioIgnoreCase(nombre)) {
            throw new OperacionNoAutorizadaException("El nombre de usuario ya está en uso.");
        }
        if (usuarioRepositorio.existsByCorreoIgnoreCase(correo)) {
            throw new OperacionNoAutorizadaException("El correo ya está registrado.");
        }

        //todo registro público nace con rol USER.
        Usuario usuario = new Usuario(nombre, correo, dto.getContrasena(), RolUsuario.USER);
        return usuarioRepositorio.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario autenticar(String identificador, String contrasena) {
        String dato = identificador == null ? "" : identificador.trim();
        if (dato.startsWith("@")) {
            dato = dato.substring(1);
        }

        // 1. Permitir acceso indistintamente con nombre de usuario o correo.
        Usuario usuario = usuarioRepositorio
                .findByNombreUsuarioIgnoreCaseOrCorreoIgnoreCase(dato, dato)
                .orElseThrow(() -> new OperacionNoAutorizadaException(
                        "Las credenciales introducidas no son correctas."));

        //Comparar la contraseña recibida con la almacenada.
        if (!usuario.getContrasena().equals(contrasena)) {
            throw new OperacionNoAutorizadaException("Las credenciales introducidas no son correctas.");
        }
        return usuario;
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepositorio.findByNombreUsuarioIgnoreCase(nombreUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }


}

