package com.api.crud.dto;

public class CrearProductoNuevoDTO {
    private String nombre;
    private Integer stock;
    private String descripcion;
    private Integer idProveedor;

    public CrearProductoNuevoDTO() {
    }

    public CrearProductoNuevoDTO(String nombre, Integer stock, String descripcion, Integer idProveedor) {
        this.nombre = nombre;
        this.stock = stock;
        this.descripcion = descripcion;
        this.idProveedor = idProveedor;
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

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
}
