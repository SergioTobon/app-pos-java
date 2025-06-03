package com.api.crud.repositories;

import com.api.crud.models.ProductoProveedorModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedorModel, Integer> {

    Optional<List<ProductoProveedorModel>> findByIdProductos(Integer id);

    Optional<ProductoProveedorModel> findByProductoIdAndProveedor_Id(Integer idProducto, Integer idProveedor);    @Transactional
    void deleteByIdProductos(Integer id);
}