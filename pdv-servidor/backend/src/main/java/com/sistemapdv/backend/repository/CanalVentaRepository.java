package com.sistemapdv.backend.repository;

import com.sistemapdv.backend.entity.CanalVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanalVentaRepository extends JpaRepository<CanalVenta, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);
}
