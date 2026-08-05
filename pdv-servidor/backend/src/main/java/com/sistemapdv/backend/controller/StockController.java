package com.sistemapdv.backend.controller;

import com.sistemapdv.backend.dto.request.StockRequestDTO;
import com.sistemapdv.backend.dto.response.StockResponseDTO;
import com.sistemapdv.backend.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/")
    public ResponseEntity<StockResponseDTO> addStock(
            @Valid @RequestBody StockRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockService.addStock(request));
    }
}
