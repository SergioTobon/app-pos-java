// src/main/java/com/api/crud/dto/DetalleVentaRequestDTO.java
package com.api.crud.dto;

public class DetalleVentaRequestDTO {
    private Integer idProducto;
    private Integer cantidad;
    private Double precioUnitario;      // Campo existente, se usará si se proporciona
    private Double porcentajeGanancia;  // ¡NUEVO CAMPO!

    // Constructor vacío (necesario para Spring/Jackson)
    public DetalleVentaRequestDTO() {
    }

    // Constructor con todos los campos (opcional, pero útil)
    public DetalleVentaRequestDTO(Integer idProducto, Integer cantidad, Double precioUnitario, Double porcentajeGanancia) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.porcentajeGanancia = porcentajeGanancia; // Inicializar nuevo campo
    }

    // --- Getters y Setters ---

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

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // --- ¡Añade estos métodos getter y setter para porcentajeGanancia! ---
    public Double getPorcentajeGanancia() {
        return porcentajeGanancia;
    }

    public void setPorcentajeGanancia(Double porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia;
    }
}