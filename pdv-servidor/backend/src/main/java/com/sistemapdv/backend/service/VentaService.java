package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
import com.sistemapdv.backend.dto.PagoDTO;
import com.sistemapdv.backend.dto.request.VentaRequestDTO;
import com.sistemapdv.backend.dto.response.VentaResponseDTO;
import com.sistemapdv.backend.entity.*;
import com.sistemapdv.backend.exception.ClosedCashException;
import com.sistemapdv.backend.exception.InvalidSaleException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.DetalleVentaMapper;
import com.sistemapdv.backend.mapper.VentaMapper;
import com.sistemapdv.backend.model.venta.DetalleVentaProcesado;
import com.sistemapdv.backend.repository.*;
import com.sistemapdv.backend.utils.enums.EstadoCaja;
import com.sistemapdv.backend.utils.enums.EstadoVenta;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class VentaService {

    private final AuthenticationService authenticationService;
    private final VentaRepository ventaRepository;
    private final CajaRepository cajaRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final CanalVentaRepository canalVentaRepository;
    private final StockRepository stockRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final ProductoCanalRepository productoCanalRepository;
    private final PagoRepository pagoRepository;
    private final DetalleVentaMapper detalleVentaMapper;
    private final VentaMapper ventaMapper;

    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    // Representa la cantidad mínima de variantes diferentes que se debe comprar para poder acceder al precio mayorista
    // de un producto con variantes
    private final int CANTIDAD_VARIANTES_DIFERENTES = 3;
    // Monto mínimo que debe superar la compra para poder obtener descuento de precios mayoristas
    private final BigDecimal MONTO_MINIMO = BigDecimal.valueOf(15000);

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> obtenerVentas(){
        return ventaRepository.findAll()
                .stream()
                .map(ventaMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRequestDTO request){

        // Validar canal de venta
        CanalVenta canalVenta = canalVentaRepository.findById(request.getIdCanalVenta())
                .orElseThrow( () -> new ResourceNotFoundException("Canal con ID "
                        + request.getIdCanalVenta() + " no encontrado"));

        Caja caja = cajaRepository.findById(request.getIdCaja())
                .orElseThrow( () -> new ResourceNotFoundException("Caja con ID "
                        + request.getIdCaja() + " no encontrada"));

        // Verificar que la sesión de caja no se encuentre cerrada
        if(caja.getEstado().equals(EstadoCaja.CERRADA))
            throw new ClosedCashException("La caja se encuentra cerrada");

        // Validar usuario logueado
        Usuario usuarioAutenticado = authenticationService.getUserAuthenticated();

        // Procesar los detalles de venta
        List<DetalleVentaProcesado> detallesProcesados = procesarDetallesVenta(request.getDetalles(), canalVenta);

        // Agrupar detalles por producto
        Map<Producto, List<DetalleVentaProcesado>> grupos =
                detallesProcesados.stream()
                        .collect(Collectors.groupingBy(
                                DetalleVentaProcesado::getProducto
                        ));

        verificarTipoVenta(grupos);

        BigDecimal totalVenta = obtenerTotalVenta(grupos);

        Venta venta = ventaMapper.toVenta(caja, usuarioAutenticado, totalVenta, request.getDescuento());

        ventaRepository.save(venta);

        crearDetallesVenta(grupos, venta);

        //Venta venta = ventaRepository.findById(venta.getIdVenta())
         //       .orElseThrow( () -> new ResourceNotFoundException("Venta no encontrada"));

        return ventaMapper.toResponseDTO(venta);
    }

    private List<DetalleVentaProcesado> procesarDetallesVenta(List<DetalleVentaDTO> detalles,
                                                              CanalVenta canalVenta){

        List<DetalleVentaProcesado> detallesProcesados = new ArrayList<>();

        for(DetalleVentaDTO detalle: detalles) {
            // Verificar existencia de la variante de producto
            VarianteProducto variante = varianteProductoRepository.findById(detalle.getIdVariante())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante con ID "
                            + detalle.getIdVariante() + " no encontrada"));

            // Valida que exista un registro de Stock para la variante en el canal de venta
            Stock stock = stockRepository.findByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
                    variante.getIdVariante(),
                    canalVenta.getIdCanalVenta()
            ).orElseThrow( () -> new ResourceNotFoundException("La variante " + variante.getNombre()
                + " no tiene un stock en el canal de venta seleccionado"));

            logger.info("Variante: {}", variante.getNombre());
            logger.info("Stock: {}", stock.getCantidadDisponible());

            // Compara si la cantidad solicitada es aceptable para la cantidad disponible
            if(detalle.getCantidad().compareTo(stock.getCantidadDisponible()) > 0){
                throw new InvalidSaleException("La variante " + variante.getNombre() + " no tiene stock suficiente");
            }

            // Obtener producto
            Producto producto = variante.getProducto();

            // Verificar que exista una configuración para Producto + Canal
            ProductoCanal productoCanal = productoCanalRepository.findByProductoIdProductoAndCanalVentaIdCanalVenta(
                    producto.getIdProducto(),
                    canalVenta.getIdCanalVenta()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "El producto " + producto.getNombre()
                                    + " no se encuentra configurado para el canal de venta seleccionado"
                    )
            );

            DetalleVentaProcesado procesado = new DetalleVentaProcesado();

            procesado.setVariante(variante);
            procesado.setProducto(producto);
            procesado.setProductoCanal(productoCanal);
            procesado.setCantidad(detalle.getCantidad());
            procesado.setPrecioUnitario(producto.getPrecioMinorista());
            procesado.setSubtotal(producto.getPrecioMinorista()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad())));

            detallesProcesados.add(procesado);
        }

        return detallesProcesados;
    }

    private void verificarTipoVenta(Map<Producto, List<DetalleVentaProcesado>> grupos){

        BigDecimal totalVenta = obtenerTotalVenta(grupos);

        logger.info("Total de la venta: {}", totalVenta);

        if(totalVenta.compareTo(MONTO_MINIMO) >= 0){
            // Seguir verificando si la compra es mayorista
        }

        /*for(Map.Entry<Producto, List<DetalleVentaProcesado>> entry : grupos.entrySet()){

            Producto producto = entry.getKey();

            List<DetalleVentaProcesado> detalles = entry.getValue();

            ProductoCanal productoCanal = detalles.get(0).getProductoCanal();

            int cantidadVariantesDiferentes =
                    (int) detalles.stream()
                            .map(detalle -> detalle.getVariante().getIdVariante())
                            .distinct()
                            .count();

            int cantidadTotal = detalles.stream()
                    .mapToInt(DetalleVentaProcesado::getCantidad)
                    .sum();

            aplicarPrecios(producto, detalles, cantidadTotal);

            logger.info("Producto: {}", producto.getNombre());
            logger.info("Producto-Canal: {}", productoCanal.getCanalVenta());
            logger.info("Variantes diferentes: {}", cantidadVariantesDiferentes);
            logger.info("Cantidad total: {}", cantidadTotal);
            logger.info("Precio: {}", detalles.get(0).getPrecioUnitario());

            // Se deben llevar al menos 3 variantes diferentes para un producto con variantes
            //boolean alcanzaCantidadMinima = cantidadVariantesDiferentes >= CANTIDAD_VARIANTES_DIFERENTES;
        }*/
    }

    private BigDecimal obtenerTotalVenta(Map<Producto, List<DetalleVentaProcesado>> grupos){
        BigDecimal totalVenta = grupos.values()
                .stream()
                .flatMap(List::stream)
                .map(detalle -> detalle.getPrecioUnitario()
                        .multiply(BigDecimal.valueOf(detalle.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalVenta;
    }

    private void crearDetallesVenta(Map<Producto, List<DetalleVentaProcesado>> grupos,
                                    Venta venta){

        for (Map.Entry<Producto, List<DetalleVentaProcesado>> entry : grupos.entrySet()){

            List<DetalleVentaProcesado> detalles = entry.getValue();

            for (DetalleVentaProcesado detalle : detalles){

                DetalleVenta nuevoDetalleVenta = detalleVentaMapper.toDetalleVenta(detalle, venta);

                detalleVentaRepository.save(nuevoDetalleVenta);
            }
        }
    }

    private void aplicarPrecios(Producto producto, List<DetalleVentaProcesado> detalles,
                                int cantidadTotal){

        ProductoCanal productoCanal = detalles.get(0).getProductoCanal();

        // Verificar si el producto sigue disponible para mayorista
        if(productoCanal.getLimiteMayorista() >= cantidadTotal){
            // Aplicar precio mayorista
            for (DetalleVentaProcesado detalle: detalles){
                detalle.setPrecioUnitario(producto.getPrecioMayorista());
            }
        }else{
            // Aplicar precio minorista
            for (DetalleVentaProcesado detalle: detalles){
                detalle.setPrecioUnitario(producto.getPrecioMinorista());
            }
        }
    }

    private void registrarPagos(List<PagoDTO> pagos, Venta venta){
        for(PagoDTO pago: pagos){
            Pago nuevoPago = new Pago();

            nuevoPago.setVenta(venta);
            nuevoPago.setMedioPago(pago.getMedioPago());
            nuevoPago.setImporte(pago.getImporte());

            pagoRepository.save(nuevoPago);
        }
    }
}
