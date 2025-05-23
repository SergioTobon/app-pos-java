package com.api.crud.repositories;

import com.api.crud.models.ProductoProveedorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedorModel, Integer> {

}