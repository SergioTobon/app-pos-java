package com.api.crud.repositories;

import com.api.crud.models.DetalleVentaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVentaModel, Integer> {
    // Puedes añadir métodos personalizados si necesitas buscar detalles por ID de venta o ID de producto
    // List<DetalleVentaModel> findByVentaIdVenta(Integer idVenta);
    // List<DetalleVentaModel> findByProductoId(Integer idProducto);
}