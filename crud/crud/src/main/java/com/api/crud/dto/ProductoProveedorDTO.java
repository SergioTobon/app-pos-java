// src/main/java/com/api/crud/dto/ProductoProveedorDTO.java
package com.api.crud.dto;

public class ProductoProveedorDTO {
    private Integer idProveedor;
    private String nombreProveedor;
    private Integer idProducto;     // Este es el campo que representa la parte del ID del producto
    private String nombreProducto;  // Para facilitar la visualización en el DTO
    private Double precioCompraEspecifico;

    // Constructor vacío (necesario para Spring/Jackson)
    public ProductoProveedorDTO() {
    }

    // Constructor con todos los campos (opcional pero útil)
    public ProductoProveedorDTO(Integer idProveedor, String nombreProveedor, Integer idProducto, String nombreProducto, Double precioCompraEspecifico) {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioCompraEspecifico = precioCompraEspecifico;
    }

    // --- Getters y Setters ---

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public Integer getIdProducto() { // Getter para el ID del producto
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) { // Setter para el ID del producto
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Double getPrecioCompraEspecifico() {
        return precioCompraEspecifico;
    }

    public void setPrecioCompraEspecifico(Double precioCompraEspecifico) {
        this.precioCompraEspecifico = precioCompraEspecifico;
    }
}