package com.sistemapdv.backend.repository.specification;

import com.sistemapdv.backend.entity.CanalVenta;
import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.Stock;
import com.sistemapdv.backend.entity.VarianteProducto;
import jakarta.persistence.criteria.Join;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
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

    public static Specification<Stock> porEstado(String estado){
        return (root, query, cb) -> {
            if(estado == null || estado.trim().isEmpty()){
                return cb.conjunction();
            }

            String estadoNormalizado = estado.trim().toLowerCase();

            switch (estadoNormalizado){
                case "disponible":
                    // cantidadDisponible > stockMinimo
                    return cb.greaterThan(root.get("cantidadDisponible"),
                            root.get("stockMinimo"));

                case "bajo":
                    // cantidadDisponible > 0 AND cantidadDisponible <= stockMinimo
                    return cb.and(
                            cb.greaterThan(root.get("cantidadDisponible"), 0),
                            cb.lessThanOrEqualTo(
                                    root.get("cantidadDisponible"),
                                    root.get("stockMinimo")
                            )
                    );

                case "sin":
                    // cantidadDisponible = 0
                    return cb.equal(root.get("cantidadDisponible"), 0);

                default:
                    // Si se envia un estado inválido, se omiten los filtros
                    return cb.conjunction();
            }
        };
    }
}
