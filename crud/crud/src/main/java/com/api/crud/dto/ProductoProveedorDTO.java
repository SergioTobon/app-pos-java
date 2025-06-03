package com.api.crud.dto;

public class ProductoProveedorDTO {
    private Integer id;
    private Integer idProducto;
    private Integer idProveedor;
    private Double precio;

    // Constructor vacío
    public ProductoProveedorDTO() {}

    // Constructor con parámetros
    public ProductoProveedorDTO(Integer id, Integer idProducto, Integer idProveedor, Double precio) {
        this.id = id;
        this.idProducto = idProducto;
        this.idProveedor = idProveedor;
        this.precio = precio;
    }

    // Getters y Setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
