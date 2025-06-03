// Archivo: DetalleCompraRepository.java
package com.api.crud.repositories;

import com.api.crud.models.DetalleCompraModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompraModel, Integer> {
    // Método para encontrar detalles de compra por el ID de la compra asociada
    List<DetalleCompraModel> findByCompra_IdCompra(Integer idCompra); // ¡Añade esta línea!
}