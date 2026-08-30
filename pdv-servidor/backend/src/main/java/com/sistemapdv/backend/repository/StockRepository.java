package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer>,
                                         JpaSpecificationExecutor<Stock> {

    List<Stock> findByVarianteProductoIdVariante(Integer idVariante);

    List<Stock> findByCanalVentaIdCanalVenta(Integer idCanal);

    Optional<Stock> findByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
            Integer idVariante,
            Integer idCanal
    );

    boolean existsByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
            Integer idVariante,
            Integer idCanal
    );

    @Query("""
    SELECT s
    FROM Stock s
    JOIN FETCH s.varianteProducto v
    JOIN FETCH s.canalVenta c
    WHERE v.producto.idProducto = :idProducto
    """)
    List<Stock> findByProductoId(Integer idProducto);

}
