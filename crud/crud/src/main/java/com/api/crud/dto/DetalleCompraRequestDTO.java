// src/main/java/com/api/crud/dto/DetalleCompraRequestDTO.java (Modificado)
package com.api.crud.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DetalleCompraRequestDTO {
    private Integer idProducto;
    private Integer cantidad;
    @JsonProperty("precioCompra")
    private Double precioCompra; // ¡Campo añadido! Precio al que se compra esta unidad en esta transacción

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

    public Double getPrecioCompra() { // Nuevo getter
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) { // Nuevo setter
        this.precioCompra = precioCompra;
    }
}