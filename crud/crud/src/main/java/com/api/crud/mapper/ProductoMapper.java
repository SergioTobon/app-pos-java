package com.api.crud.mapper;

import com.api.crud.dto.ProductoDTO;
import com.api.crud.models.ProductoModel;

public class ProductoMapper {

    public static ProductoDTO toDTO(ProductoModel producto) {
        if (producto == null) {
            return null;
        }

        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setStock(producto.getStock());
        dto.setDescripcion(producto.getDescripcion());
        return dto;
    }

    public static ProductoModel toEntity(ProductoDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductoModel producto = new ProductoModel();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setStock(dto.getStock());
        producto.setDescripcion(dto.getDescripcion());
        return producto;
    }
}
