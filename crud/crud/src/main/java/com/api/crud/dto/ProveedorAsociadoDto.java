// src/main/java/com/api/crud/dtos/ProveedorAsociadoDto.java
package com.api.crud.dto;

public class ProveedorAsociadoDto {
    private Integer idProveedor;
    private Double precioCompraEspecifico; // Coincide con la clave de tu JSON

    // Getters y Setters
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }
    public Double getPrecioCompraEspecifico() { return precioCompraEspecifico; }
    public void setPrecioCompraEspecifico(Double precioCompraEspecifico) { this.precioCompraEspecifico = precioCompraEspecifico; }
}