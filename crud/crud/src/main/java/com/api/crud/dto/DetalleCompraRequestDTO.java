package com.api.crud.dto;

public class DetalleCompraRequestDTO {
    private Integer idProducto;
    private Integer cantidad;

    // Constructor vacío
    public DetalleCompraRequestDTO() {}

    // Constructor con parámetros
    public DetalleCompraRequestDTO(Integer idProducto, Integer cantidad) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}