package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<MovimientoStock, Integer> {
}
