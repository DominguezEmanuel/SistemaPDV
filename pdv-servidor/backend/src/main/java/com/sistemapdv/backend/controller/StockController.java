package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.StockRequestDTO;
import com.sistemapdv.backend.dto.response.StockResponseDTO;
import com.sistemapdv.backend.service.StockService;
import com.sistemapdv.backend.utils.enums.EstadoStock;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponseDTO> getStockById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.getStockById(id));
    }

    @GetMapping("/")
    public PagedModel<StockResponseDTO> getAllStocks(Pageable pageable) {

        Page<StockResponseDTO> registros = stockService.getAllStocks(pageable);

        return new PagedModel<>(registros);
    }

    @GetMapping("/variante/{idVariante}")
    public ResponseEntity<List<StockResponseDTO>> getStocksByIdVariant(@PathVariable Integer idVariante) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.getStocksByIdVariant(idVariante));
    }

    @GetMapping("/canal/{idCanal}")
    public ResponseEntity<List<StockResponseDTO>> getStocksByIdChannel(@PathVariable Integer idCanal) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.getStocksByIdChannel(idCanal));
    }

    @GetMapping("/canal/{idCanal}/variante/{idVariante}")
    public ResponseEntity<StockResponseDTO> getStockByChannelAndVariant(@PathVariable Integer idCanal,
                                                                        @PathVariable Integer idVariante){
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.getStockByChannelAndVariant(idCanal, idVariante));
    }

    @GetMapping("/buscar")
    public PagedModel<StockResponseDTO> getStockByFilters(@RequestParam (required = false) String nombre,
                                                          @RequestParam (required = false) Integer idCanal,
                                                          @RequestParam (required = false) EstadoStock estado,
                                                          Pageable pageable){
        Page<StockResponseDTO> registrosFiltrados = stockService.getByFilters(nombre, idCanal, estado, pageable);

        return new PagedModel<>(registrosFiltrados);
    }

    @PostMapping("/")
    public ResponseEntity<StockResponseDTO> createStock(
            @Valid @RequestBody StockRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockService.createStock(request));
    }

    /* De esto se encargará MovimientoStock */
    @PatchMapping("/cantidad/{idStock}")
    public ResponseEntity<StockResponseDTO> editStockAvailable(@PathVariable Integer idStock,
                                                               @RequestParam Integer nuevaCantidad){
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.editStock(idStock, nuevaCantidad));
    }

    @PatchMapping("/stock-minimo/{id}")
    public ResponseEntity<StockResponseDTO> editStockMinimo(@PathVariable Integer id,
                                                            @RequestParam Integer nuevoStockMinimo){
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.editStockMinimo(id, nuevoStockMinimo));
    }
}
