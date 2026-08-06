package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.ProductoCanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoCanalRepository extends JpaRepository<ProductoCanal, Integer> {

    List<ProductoCanal> findByProductoIdProducto(Integer idProducto);

    List<ProductoCanal> findByCanalVentaIdCanalVenta(Integer idCanal);

    Optional<ProductoCanal>
    findByProductoIdProductoAndCanalVentaIdCanalVenta(
            Integer idProducto,
            Integer idCanal);

    boolean existsByProductoIdProductoAndCanalVentaIdCanalVenta(
            Integer idProducto,
            Integer idCanal);
}
