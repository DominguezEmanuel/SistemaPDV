package com.sistemapdv.backend.service;

import com.sistemapdv.backend.dto.DetalleVentaDTO;
import com.sistemapdv.backend.dto.PagoDTO;
import com.sistemapdv.backend.dto.request.VentaRequestDTO;
import com.sistemapdv.backend.dto.response.VentaResponseDTO;
import com.sistemapdv.backend.entity.*;
import com.sistemapdv.backend.exception.ClosedCashException;
import com.sistemapdv.backend.exception.ResourceNotFoundException;
import com.sistemapdv.backend.mapper.DetalleVentaMapper;
import com.sistemapdv.backend.mapper.VentaMapper;
import com.sistemapdv.backend.repository.*;
import com.sistemapdv.backend.utils.enums.EstadoCaja;
import com.sistemapdv.backend.utils.enums.EstadoVenta;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class VentaService {

    private final AuthenticationService authenticationService;
    private final VentaRepository ventaRepository;
    private final CajaRepository cajaRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final PagoRepository pagoRepository;
    private final DetalleVentaMapper detalleVentaMapper;
    private final VentaMapper ventaMapper;

    @Transactional(readOnly = true)
    public List<VentaResponseDTO> getAllSales(){
        return ventaRepository.findAll()
                .stream()
                .map(ventaMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public VentaResponseDTO registerSale(VentaRequestDTO request){

        Caja caja = cajaRepository.findById(request.getIdCaja())
                .orElseThrow( () -> new ResourceNotFoundException("Caja con ID "
                        + request.getIdCaja() + " no encontrada"));

        if(caja.getEstado().equals(EstadoCaja.CERRADA))
            throw new ClosedCashException("La caja se encuentra cerrada");

        Usuario usuarioAutenticado = authenticationService.getUserAuthenticated();

        Venta venta = new Venta();

        venta.setCaja(caja);
        venta.setUsuario(usuarioAutenticado);
        venta.setFechaHora(OffsetDateTime.now());

        venta.setSubtotal(BigDecimal.ZERO);
        venta.setDescuento(BigDecimal.ZERO);
        venta.setTotal(BigDecimal.ZERO);
        venta.setEstado(EstadoVenta.COMPLETADA);

        ventaRepository.save(venta);

        createDetails(request.getDetalles(), venta);

        registerPayments(request.getPagos(), venta);

        Venta ventaGuardada = ventaRepository.findById(venta.getIdVenta())
                .orElseThrow( () -> new ResourceNotFoundException("Venta con ID "
                        + venta.getIdVenta() + " no encontrada"));

        return ventaMapper.toResponseDTO(ventaGuardada);
    }

    private void createDetails(List<DetalleVentaDTO> detalles, Venta venta){
        for(DetalleVentaDTO detalle: detalles){
            VarianteProducto variante = varianteProductoRepository.findById(detalle.getIdVariante())
                    .orElseThrow( () -> new ResourceNotFoundException("Variante con ID "
                    + detalle.getIdVariante() + " no encontrada"));

            DetalleVenta nuevoDetalle = new DetalleVenta();

            nuevoDetalle.setVariante(variante);
            nuevoDetalle.setVenta(venta);
            nuevoDetalle.setCantidad(detalle.getCantidad());
            nuevoDetalle.setPrecioUnitario(detalle.getPrecioUnitario());
            nuevoDetalle.setSubtotal(detalle.getPrecioUnitario()
                    .multiply(
                            BigDecimal.valueOf(detalle.getCantidad())
                    )
            );

            detalleVentaRepository.save(nuevoDetalle);
        }
    }

    private void registerPayments(List<PagoDTO> pagos, Venta venta){
        for(PagoDTO pago: pagos){
            Pago nuevoPago = new Pago();

            nuevoPago.setVenta(venta);
            nuevoPago.setMedioPago(pago.getMedioPago());
            nuevoPago.setImporte(pago.getImporte());

            pagoRepository.save(nuevoPago);
        }
    }
}
