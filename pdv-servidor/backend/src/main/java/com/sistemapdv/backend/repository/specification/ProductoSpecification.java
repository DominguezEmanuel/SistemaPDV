package com.sistemapdv.backend.repository.specification;

import com.sistemapdv.backend.entity.Producto;
import org.springframework.data.jpa.domain.Specification;

public class ProductoSpecification {

    public static Specification<Producto> nombreContiene(String nombre){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"
                );
    }

    public static Specification<Producto> perteneceACategoria(Integer idCategoria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("categoria").get("idCategoria"),
                        idCategoria
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
