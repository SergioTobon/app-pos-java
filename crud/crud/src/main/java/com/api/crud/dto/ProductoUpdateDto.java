// src/main/java/com/api/crud/dtos/ProductoUpdateDto.java
package com.api.crud.dto;

import java.util.List;

public class ProductoUpdateDto {
    private String nombre;
    private String descripcion;
    private Integer stock;
    private Double precioVenta;
    private List<ProveedorAsociadoDto> proveedoresAsociados; // Coincide con la clave de tu JSON

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    public List<ProveedorAsociadoDto> getProveedoresAsociados() { return proveedoresAsociados; }
    public void setProveedoresAsociados(List<ProveedorAsociadoDto> proveedoresAsociados) { this.proveedoresAsociados = proveedoresAsociados; }
}