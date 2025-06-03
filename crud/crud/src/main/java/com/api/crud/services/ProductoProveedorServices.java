package com.api.crud.services;

import com.api.crud.dto.ProductoProveedorDTO;
import com.api.crud.repositories.ProductoProveedorRepository;
import com.api.crud.mapper.ProductoProveedorMapper;
import com.api.crud.models.ProductoProveedorModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoProveedorServices {

    private final ProductoProveedorRepository productoProveedorRepository;

    public ProductoProveedorServices(ProductoProveedorRepository productoProveedorRepository) {
        this.productoProveedorRepository = productoProveedorRepository;
    }

    public List<ProductoProveedorDTO> listarProductoProveedores() {
        return productoProveedorRepository.findAll().stream()
                .map(ProductoProveedorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ProductoProveedorDTO obtenerProductoProveedor(Integer id) {
        return productoProveedorRepository.findById(id)
                .map(ProductoProveedorMapper::toDTO)
                .orElse(null);
    }

    public ProductoProveedorDTO guardarProductoProveedor(ProductoProveedorDTO productoProveedorDTO) {
        ProductoProveedorModel productoProveedor = ProductoProveedorMapper.toEntity(productoProveedorDTO);
        productoProveedor = productoProveedorRepository.save(productoProveedor);
        return ProductoProveedorMapper.toDTO(productoProveedor);
    }

    public ProductoProveedorDTO actualizarProductoProveedor(Integer id, ProductoProveedorDTO productoProveedorDTO) {
        if (!productoProveedorRepository.existsById(id)) {
            return null;
        }
        ProductoProveedorModel productoProveedor = ProductoProveedorMapper.toEntity(productoProveedorDTO);
        productoProveedor.setId(id);
        productoProveedor = productoProveedorRepository.save(productoProveedor);
        return ProductoProveedorMapper.toDTO(productoProveedor);
    }

    public void eliminarProductoProveedor(Integer id) {
        productoProveedorRepository.deleteById(id);
    }
}
