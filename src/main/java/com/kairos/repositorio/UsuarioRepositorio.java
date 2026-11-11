package com.kairos.repositorio;

import com.kairos.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByNombreUsuarioIgnoreCaseOrCorreoIgnoreCase(String nombreUsuario, String correo);

    boolean existsByNombreUsuarioIgnoreCase(String nombreUsuario);

    boolean existsByCorreoIgnoreCase(String correo);
}

