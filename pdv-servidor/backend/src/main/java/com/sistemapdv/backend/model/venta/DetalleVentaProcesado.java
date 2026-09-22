package com.sistemapdv.backend.model.venta;

import com.sistemapdv.backend.entity.Producto;
import com.sistemapdv.backend.entity.ProductoCanal;
import com.sistemapdv.backend.entity.VarianteProducto;
import com.sistemapdv.backend.entity.Venta;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVentaProcesado {

    private VarianteProducto variante;

    private Producto producto;

    private ProductoCanal productoCanal;

    private Integer cantidad;

    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
}
