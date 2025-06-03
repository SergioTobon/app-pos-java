package com.api.crud.dto;

import java.util.List;

public class CompraRequestDTO {
    private Integer idProveedor;
    private List<DetalleCompraRequestDTO> productos; // Lista de productos en esta compra

    // Constructor vacío
    public CompraRequestDTO() {}

    // Constructor con parámetros
    public CompraRequestDTO(Integer idProveedor, List<DetalleCompraRequestDTO> productos) {
        this.idProveedor = idProveedor;
        this.productos = productos;
    }

    // Getters y Setters
    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public List<DetalleCompraRequestDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<DetalleCompraRequestDTO> productos) {
        this.productos = productos;
    }
}