package com.sistemapdv.backend.repository.specification;

import com.sistemapdv.backend.entity.CanalVenta;
import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.entity.VarianteProducto;
import com.sistemapdv.backend.utils.enums.EstadoStock;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class StockSpecification {

    public static Specification<Stock> buscarPorTexto(String texto) {
        Specification<Stock> buscar = (root, query, cb) -> {

            if (texto == null || texto.trim().isEmpty()) {
                return cb.conjunction();
            }

            Join<Stock, VarianteProducto> variante =
                    root.join("varianteProducto");

            Join<VarianteProducto, Producto> producto =
                    variante.join("producto");

            String valor = "%" + texto.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(producto.get("nombre")), valor),
                    cb.like(cb.lower(variante.get("nombre")), valor),
                    cb.like(cb.lower(variante.get("codigoInterno")), valor),
                    cb.like(cb.lower(variante.get("codigoBarras")), valor)
            );
        };
        return buscar;
    }

    public static Specification<Stock> porCanal(Integer idCanalVenta){
        Specification<Stock> buscar = (root, query, cb) ->{
            if(idCanalVenta == null){
                return cb.conjunction();
            }

            Join<Stock, CanalVenta> canal = root.join("canalVenta");

            return cb.equal(
                    canal.get("idCanalVenta"),
                    idCanalVenta
            );
        };
        return buscar;
    }

    public static Specification<Stock> porEstado(EstadoStock estado) {
        return (root, query, cb) -> {

            if (estado == null) {
                return cb.conjunction();
            }

            try {

                return cb.equal(
                        root.get("estado"),
                        estado
                );

            } catch (IllegalArgumentException e) {
                return cb.conjunction();
            }
        };
    }
}
