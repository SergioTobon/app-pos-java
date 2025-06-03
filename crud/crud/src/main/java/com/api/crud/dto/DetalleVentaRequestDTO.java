package com.api.crud.dto;

public class DetalleVentaRequestDTO {

    private Integer idProducto;
    private Integer cantidad;
    private Double porcentajeGanancia; // <-- ¡Nuevo campo!
    // No se necesita precioUnitario aquí, ya que el servicio lo calculará o lo tomará del ProductoModel

    public DetalleVentaRequestDTO() {
    }

    // Actualiza el constructor para incluir el nuevo campo
    public DetalleVentaRequestDTO(Integer idProducto, Integer cantidad, Double porcentajeGanancia) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.porcentajeGanancia = porcentajeGanancia; // Asigna el nuevo campo
    }

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

    public Double getPorcentajeGanancia() {
        return porcentajeGanancia;
    }

    public void setPorcentajeGanancia(Double porcentajeGanancia) {
        this.porcentajeGanancia = porcentajeGanancia;
    }
}