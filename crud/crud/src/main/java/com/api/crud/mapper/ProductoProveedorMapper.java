package com.api.crud.mapper;
import com.api.crud.dto.ProductoProveedorDTO;
import com.api.crud.models.ProductoProveedorModel;

public class ProductoProveedorMapper {

    public static ProductoProveedorDTO toDTO(ProductoProveedorModel productoProveedor) {
        if (productoProveedor == null) {
            return null;
        }

        ProductoProveedorDTO dto = new ProductoProveedorDTO();
        dto.setId(productoProveedor.getId());
        dto.setPrecio(productoProveedor.getPrecio());
        dto.setIdProducto(productoProveedor.getProducto().getId());
        dto.setIdProveedor(productoProveedor.getProveedor().getId());
        return dto;
    }

    public static ProductoProveedorModel toEntity(ProductoProveedorDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductoProveedorModel productoProveedor = new ProductoProveedorModel();
        productoProveedor.setId(dto.getId());
        productoProveedor.setPrecio(dto.getPrecio());

        return productoProveedor;
    }
}
