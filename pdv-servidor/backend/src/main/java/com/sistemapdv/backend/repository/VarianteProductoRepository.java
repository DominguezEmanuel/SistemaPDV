package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.VarianteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Integer> {

    boolean existsById(Integer id);

    @Query("""
            SELECT vp
            FROM VarianteProducto vp
            WHERE vp.producto.id = :idProducto
            """)
    List<VarianteProducto> findByProductoId(@Param("idProducto") Integer idProducto);

    @Query("""
            SELECT vp
            FROM VarianteProducto vp
            WHERE vp.activo = true
            """)
    List<VarianteProducto> findByActivoTrue();

    @Query("""
            SELECT vp
            FROM VarianteProducto vp
            WHERE vp.codigoBarras = :codigoBarras
            """)
    Optional<VarianteProducto> findByCodigoBarras(@Param("codigoBarras") String codigoBarras);

    @Query("""
            SELECT COUNT(vp) > 0
            FROM VarianteProducto vp
            WHERE vp.codigoBarras = :codigoBarras
            """)
    boolean existsByCodigoBarras(@Param("codigoBarras") String codigoBarras);
}
