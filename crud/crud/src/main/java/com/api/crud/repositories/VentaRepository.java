// src/main/java/com/api/crud/repositories/VentaRepository.java
package com.api.crud.repositories;

import com.api.crud.models.VentaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<VentaModel, Integer> {
    // Cuenta el número de ventas en un rango de fechas
    @Query("SELECT COUNT(v) FROM VentaModel v WHERE v.fecha BETWEEN :startDate AND :endDate")
    Long countVentasBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}