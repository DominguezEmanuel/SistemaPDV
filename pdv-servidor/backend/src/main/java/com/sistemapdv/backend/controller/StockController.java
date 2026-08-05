package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.StockRequestDTO;
import com.sistemapdv.backend.dto.response.StockResponseDTO;
import com.sistemapdv.backend.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
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
    @ResponseBody
    public List<StockResponseDTO> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/variante/{idVariante}")
    @ResponseBody
    public List<StockResponseDTO> getStocksByIdVariant(@PathVariable Integer idVariante) {
        return stockService.getStocksByIdVariant(idVariante);
    }

    @GetMapping("/canal/{idCanal}")
    @ResponseBody
    public List<StockResponseDTO> getStocksByIdChannel(@PathVariable Integer idCanal) {
        return stockService.getStocksByIdChannel(idCanal);
    }

    // Posible cambio: usar @RequestParam para recibir ambos IDs
    @GetMapping("/canal/{idCanal}/variante/{idVariante}")
    public ResponseEntity<StockResponseDTO> getStockByChannelAndVariant(@PathVariable Integer idCanal,
                                                                        @PathVariable Integer idVariante){
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.getStockByChannelAndVariant(idCanal, idVariante));
    }

    @PostMapping("/")
    public ResponseEntity<StockResponseDTO> addStock(
            @Valid @RequestBody StockRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockService.addStock(request));
    }

    @PatchMapping("/cantidad/{id}")
    public ResponseEntity<StockResponseDTO> editCantidadDisponible(@PathVariable Integer id,
                                            @RequestParam Integer nuevaCantidad){
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.editCantidadDisponible(id, nuevaCantidad));
    }

    @PatchMapping("/stock-minimo/{id}")
    public ResponseEntity<StockResponseDTO> editStockMinimo(@PathVariable Integer id,
                                                            @RequestParam Integer nuevoStockMinimo){
        return ResponseEntity.status(HttpStatus.OK)
                .body(stockService.editStockMinimo(id, nuevoStockMinimo));
    }
}
