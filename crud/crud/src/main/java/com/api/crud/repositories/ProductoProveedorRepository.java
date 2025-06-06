// src/main/java/com/api/crud/repositories/ProductoProveedorRepository.java
package com.api.crud.repositories;

import com.api.crud.models.ProductoProveedorModel;
import com.api.crud.models.ProductoProveedorId; // Asegúrate de tener esta importación
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedorModel, ProductoProveedorId> {

    // Método para encontrar relaciones Producto-Proveedor por el ID del Producto
    // El 'id' se refiere al campo ProductoProveedorId en ProductoProveedorModel.
    // El '_IdProducto' se refiere al campo 'idProducto' dentro de ProductoProveedorId.
    List<ProductoProveedorModel> findById_IdProducto(Integer idProducto);

    // Método para encontrar relaciones Producto-Proveedor por el ID del Proveedor
    List<ProductoProveedorModel> findById_IdProveedor(Integer idProveedor);

    // Si también necesitas buscar por ambos IDs (aunque findById ya lo hace con ProductoProveedorId)
    // Optional<ProductoProveedorModel> findById_IdProductoAndId_IdProveedor(Integer idProducto, Integer idProveedor);

    // Otros métodos si los tienes...
}