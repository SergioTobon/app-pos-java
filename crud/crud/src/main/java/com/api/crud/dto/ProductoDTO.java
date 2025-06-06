// src/main/java/com/api/crud/dto/ProductoDTO.java
package com.api.crud.dto;

import java.util.List;

public class ProductoDTO {
    private Integer id;
    private String nombre;
    private Integer stock;
    private String descripcion;
    private Double precioVenta;
    private Double precioCompra;

    // ¡Aquí está el cambio! Ahora usa ProductoProveedorDTO
    private List<ProductoProveedorDTO> proveedoresAsociados;

    // Getters y Setters
    public Integer getId() {        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    // Getters y Setters para la nueva lista de DTOs
    public List<ProductoProveedorDTO> getProveedoresAsociados() {
        return proveedoresAsociados;
    }

    public void setProveedoresAsociados(List<ProductoProveedorDTO> proveedoresAsociados) {
        this.proveedoresAsociados = proveedoresAsociados;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }
}