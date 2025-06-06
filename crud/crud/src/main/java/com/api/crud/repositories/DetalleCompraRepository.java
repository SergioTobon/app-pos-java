// src/main/java/com/api/crud/repositories/DetalleCompraRepository.java (Ejemplo)
package com.api.crud.repositories;

import com.api.crud.models.DetalleCompraModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompraModel, Integer> {
    // Si usas este método en tu servicio, asegúrate de que esté aquí:
    List<DetalleCompraModel> findByCompra_IdCompra(Integer idCompra);
}