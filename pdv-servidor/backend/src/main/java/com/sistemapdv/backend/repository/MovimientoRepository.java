package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<MovimientoStock, Integer> {

    List<MovimientoStock> findTop5ByStockIdStockOrderByFechaHoraDesc(Integer idStock);
}
