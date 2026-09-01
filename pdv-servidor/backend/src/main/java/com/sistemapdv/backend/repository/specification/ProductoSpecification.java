package com.sistemapdv.backend.repository.specification;

import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.VarianteProducto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecification {

    public static Specification<Producto> nombreContiene(String texto){
        Specification<Producto> busqueda = (root, query, cb) -> {

            if(texto == null || texto.trim().isEmpty()){
                return cb.conjunction();
            }

            query.distinct(true);

            Join<Producto, VarianteProducto> variante = root.join("variantes", JoinType.LEFT);

            String valor = "%" + texto.toLowerCase() + "%";

            return cb.or(
                    cb.like(
                            cb.lower(root.get("nombre")),
                            valor
                    ),

                    cb.like(
                            cb.lower(variante.get("nombre")),
                            valor
                    ),

                    cb.like(
                            cb.lower(variante.get("codigoBarras")),
                            valor
                    )
            );
        };

        return busqueda;

        /*return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"
                );*/
    }

    public static Specification<Producto> perteneceACategoria(Integer idCategoria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("categoria").get("idCategoria"),
                        idCategoria
                );
    }

    public static Specification<Producto> contieneNombre(String nombre){
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"
                );
    }

    public static Specification<Producto> tieneEstado(Boolean activo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("activo"),
                        activo
                );
    }
}
