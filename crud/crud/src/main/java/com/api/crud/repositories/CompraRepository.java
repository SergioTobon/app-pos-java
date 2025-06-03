// src/main/java/com/api/crud/repositories/CompraRepository.java
package com.api.crud.repositories;

import com.api.crud.models.CompraModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CompraRepository extends JpaRepository<CompraModel, Integer> {
    // Suma el total de compras en un rango de fechas
    @Query("SELECT SUM(c.total) FROM CompraModel c WHERE c.fecha BETWEEN :startDate AND :endDate")
    Double sumTotalComprasBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}