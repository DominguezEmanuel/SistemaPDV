package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoTrue();

    Optional<Producto> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsById(Integer id);

    @Query("""
        SELECT p
        FROM Producto p
        JOIN FETCH p.categoria
    """)
    List<Producto> findAllWithCategoria();

    @Query("""
        SELECT p
        FROM Producto p
        JOIN FETCH p.categoria
        WHERE p.idProducto = :id
    """)
    Optional<Producto> findByIdWithCategoria(Integer id);

    @Query("""
        SELECT p
        FROM Producto p
        JOIN FETCH p.categoria
        WHERE p.nombre = :nombre
    """)
    Optional<Producto> findByNombreWithCategoria(String nombre);
}
