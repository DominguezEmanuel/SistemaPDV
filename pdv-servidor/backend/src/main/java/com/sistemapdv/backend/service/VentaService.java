package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
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
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    private final int MINIMO_VARIANTES_DIFERENTES = 3;
    private final BigDecimal MONTO_MINIMO = BigDecimal.valueOf(15000);

    // Aplicar paginación
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> obtenerVentas(){
        return ventaRepository.findAll()
                .stream()
                .map(ventaMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public VentaResponseDTO registrarVenta(VentaRequestDTO request){

        // Validar canal de venta seleccionado
        CanalVenta canalVenta = canalVentaRepository.findById(request.getIdCanalVenta())
                .orElseThrow( () -> new ResourceNotFoundException("Canal con ID "
                        + request.getIdCanalVenta() + " no encontrado"));

        // Validar caja
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
                        .collect(Collectors.groupingBy(DetalleVentaProcesado::getProducto));

        verificarTipoVenta(grupos);

        BigDecimal subtotalVenta = obtenerTotalVenta(grupos);

        Venta nuevaVenta = ventaMapper.toVenta(caja, usuarioAutenticado, subtotalVenta, request.getDescuento());

        ventaRepository.save(nuevaVenta);

        crearDetallesVenta(grupos, nuevaVenta);

        return ventaMapper.toResponseDTO(nuevaVenta);
    }

    private List<DetalleVentaProcesado> procesarDetallesVenta(List<DetalleVentaDTO> detalles,
                                                              CanalVenta canalVenta){

        List<DetalleVentaProcesado> detallesProcesados = new ArrayList<>();

        for(DetalleVentaDTO detalle: detalles) {
            // Verificar existencia de la variante de producto
            VarianteProducto variante = varianteProductoRepository.findById(detalle.getIdVariante())
                    .orElseThrow(() -> new ResourceNotFoundException("Variante con ID "
                            + detalle.getIdVariante() + " no encontrada"));

            if(!variante.getActivo()){
                throw new InvalidSaleException("La variante con ID " + variante.getIdVariante()
                        + " se encuentra inactiva");
            }

            // Obtener producto de la variante
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

            // Validar que exista un registro de stock para la variante en el canal de venta seleccionado
            Stock stock = stockRepository.findByVarianteProductoIdVarianteAndCanalVentaIdCanalVenta(
                    variante.getIdVariante(),
                    canalVenta.getIdCanalVenta()
            ).orElseThrow( () ->
                    new ResourceNotFoundException(
                            "La variante con ID " + variante.getIdVariante()
                                    + " no tiene un stock en el canal de venta seleccionado"
                    )
            );

            logger.info("Producto asociado: {}", producto.getNombre());
            logger.info("ID de variante: {}", variante.getIdVariante());
            logger.info("Nombre de variante: {}", variante.getNombre());
            logger.info("Canal de venta: {}", canalVenta.getNombre());
            logger.info("Stock disponible: {}", stock.getCantidadDisponible());

            // Compara si la cantidad solicitada es aceptable para la cantidad disponible
            if(detalle.getCantidad().compareTo(stock.getCantidadDisponible()) > 0){
                throw new InvalidSaleException("La variante con ID " + variante.getIdVariante()
                        + " no tiene stock suficiente");
            }

            DetalleVentaProcesado procesado = new DetalleVentaProcesado();

            procesado.setVariante(variante);
            procesado.setProducto(producto);
            procesado.setProductoCanal(productoCanal);
            procesado.setCantidad(detalle.getCantidad());
            // Se aplica el precio minorista al inicio
            procesado.setPrecioUnitario(producto.getPrecioMinorista());
            procesado.setSubtotal(
                    producto.getPrecioMinorista()
                            .multiply(BigDecimal.valueOf(detalle.getCantidad())
                    )
            );

            detallesProcesados.add(procesado);
        }

        return detallesProcesados;
    }

    private void verificarTipoVenta(Map<Producto, List<DetalleVentaProcesado>> grupos){

        BigDecimal subtotalVenta = obtenerTotalVenta(grupos);

        logger.info("Subtotal de la venta: ${}", subtotalVenta);

        // Monto mínimo que debe alcanzar la compra para poder acceder a precios mayoristas
        if(subtotalVenta.compareTo(MONTO_MINIMO) >= 0){
            // Procesar agrupación de detalles por producto
            for (Map.Entry<Producto, List<DetalleVentaProcesado>> entry : grupos.entrySet()){

                Producto producto = entry.getKey();

                List<DetalleVentaProcesado> detalles = entry.getValue();

                ProductoCanal productoCanal = detalles.get(0).getProductoCanal();

                CanalVenta canalVenta = productoCanal.getCanalVenta();

                Integer stockTotalProducto = stockRepository.findStockTotalByProductoIdAndCanalVentaId(
                        producto.getIdProducto(),
                        canalVenta.getIdCanalVenta()
                );

                logger.info("Agrupación del producto: {}", producto.getNombre());
                logger.info("Stock total: {}", stockTotalProducto);
                logger.info("Limite mayorista: {}", productoCanal.getLimiteMayorista());
                logger.info("Canal de venta seleccionado: {}", canalVenta.getNombre());

                // Verificar si el producto sigue habilitado para venderse por mayor
                if(stockTotalProducto.compareTo(productoCanal.getLimiteMayorista()) > 0){
                    verificarTipoProducto(producto, detalles);
                }
            }
        }
    }

    private void verificarTipoProducto(Producto producto, List<DetalleVentaProcesado> detalles){

        // Sumar las cantidades que se están comprando
        int cantidadTotal = detalles.stream()
                .mapToInt(DetalleVentaProcesado::getCantidad)
                .sum();

        logger.info("Cantidad total: {}", cantidadTotal);

        // Verificar tipo de producto: 'con variantes' o 'sin variantes'
        if(producto.getTieneVariantes()){
            // Calcular la cantidad de variantes diferentes
            int cantidadVariantesDiferentes =
                    (int) detalles.stream()
                            .map(detalle -> detalle.getVariante().getIdVariante())
                            .distinct()
                            .count();

            logger.info("Cantidad de variantes diferentes: {}", cantidadVariantesDiferentes);

            if(cantidadTotal >= producto.getMinimoMayorista() &&
                    cantidadVariantesDiferentes >= MINIMO_VARIANTES_DIFERENTES){
                // Se aplica precio mayorista para todos los detalles del producto
                aplicarPrecioDetalle(producto.getPrecioMayorista(), detalles);
            }
        }else{
            if(cantidadTotal >= producto.getMinimoMayorista()){
                // Se aplica precio mayorista para el detalle del producto
                aplicarPrecioDetalle(producto.getPrecioMayorista(), detalles);
            }
        }
    }

    private void aplicarPrecioDetalle(BigDecimal precio, List<DetalleVentaProcesado> detalles){
        logger.info("Precio aplicado: ${}", precio);
        for(DetalleVentaProcesado detalle : detalles){
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(
                    precio.multiply(BigDecimal.valueOf(detalle.getCantidad()))
            );
        }
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

    private void crearDetallesVenta(Map<Producto, List<DetalleVentaProcesado>> grupos, Venta venta){

        for (Map.Entry<Producto, List<DetalleVentaProcesado>> entry : grupos.entrySet()){

            List<DetalleVentaProcesado> detalles = entry.getValue();

            for (DetalleVentaProcesado detalle : detalles){

                DetalleVenta nuevoDetalleVenta = detalleVentaMapper.toDetalleVenta(detalle, venta);

                detalleVentaRepository.save(nuevoDetalleVenta);
            }
        }
    }
}
