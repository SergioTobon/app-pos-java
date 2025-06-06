// src/main/java/com/api/crud/repositories/ProductoRepository.java
package com.api.crud.repositories;

import com.api.crud.models.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.api.crud.dto.ProductSalesDTO; // Importa el nuevo DTO
import org.springframework.data.domain.Pageable; // Importa Pageable

@Repository
public interface ProductoRepository extends JpaRepository<ProductoModel, Integer> {
    List<ProductoModel> findByNombreContainingIgnoreCase(String nombre);

    // Query para obtener el total de productos
    @Query("SELECT COUNT(p) FROM ProductoModel p")
    Long countTotalProductos();


    // Esto es una JPQL, ajusta el nombre de las entidades y campos si son diferentes.
    @Query("SELECT new com.api.crud.dto.ProductSalesDTO(dv.producto.nombre, SUM(dv.cantidad)) " +
            "FROM DetalleVentaModel dv " +
            "GROUP BY dv.producto.nombre " +
            "ORDER BY SUM(dv.cantidad) DESC")
    List<ProductSalesDTO> findTopSellingProducts(Pageable pageable);
}