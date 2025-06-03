package com.api.crud.dto;

import java.util.List;

public class ProductoConProveedorDTO {
    private Integer id;
    private String nombre;
    private Integer stock;
    private String descripcion;
    private List<ProveedorDTO> proveedores;

    public ProductoConProveedorDTO(Integer id, String nombre, Integer stock, String descripcion, List<ProveedorDTO> proveedores) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.descripcion = descripcion;
        this.proveedores = proveedores;
    }

    // Getters y Setters


    public Integer getId() {
        return id;
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

    public List<ProveedorDTO> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<ProveedorDTO> proveedores) {
        this.proveedores = proveedores;
    }
}
