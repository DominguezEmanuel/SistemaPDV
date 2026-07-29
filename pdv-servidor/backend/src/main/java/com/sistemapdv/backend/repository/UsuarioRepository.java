package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // READ - Búsquedas personalizadas.
    // Optional: contenedor que puede contener o no un valor.
    // Es una forma segura de manejar la ausencia de datos sin usar null.
    Optional<Usuario> findByUsername(String username);

    //Optional<Usuario> findByUsernameAndActivo(String username, Boolean activo);

    //List<Usuario> findByActivo(Boolean activo);

    //List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByUsername(String username);
}
