package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.ProductoCanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoCanalRepository extends JpaRepository<ProductoCanal, Integer> {

    // Todos los canales asociados a un producto
    List<ProductoCanal> findByProductoIdProducto(Integer idProducto);

    // Todos los canales asociados a una venta
    List<ProductoCanal> findByCanalVentaIdCanalVenta(Integer idCanal);

    // Asociación específica Producto + Canal
    Optional<ProductoCanal>
    findByProductoIdProductoAndCanalVentaIdCanalVenta(
            Integer idProducto,
            Integer idCanal);

    // Verificar si ya existe una asociación
    boolean existsByProductoIdProductoAndCanalVentaIdCanalVenta(
            Integer idProducto,
            Integer idCanal);
}
