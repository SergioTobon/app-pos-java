// src/main/java/com/api/crud/dto/dashboard/ProductSalesDTO.java
package com.api.crud.dto;

public class ProductSalesDTO {
    private String nombreProducto;
    private Long cantidadVendida; // O Double si la cantidad puede ser decimal

    public ProductSalesDTO(String nombreProducto, Long cantidadVendida) {
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
    }

    // Getters y Setters
    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
}