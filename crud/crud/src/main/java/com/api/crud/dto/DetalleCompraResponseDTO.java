// Archivo: DetalleCompraResponseDTO.java
package com.api.crud.dto;

import com.api.crud.dto.ProductoDTO;

public class DetalleCompraResponseDTO {
    private Integer idDetalleCompra;
    private Integer idCompra; // ¡Campo añadido!
    private ProductoDTO producto;
    private Integer cantidad;
    private Double precioCompra;

    public DetalleCompraResponseDTO() {}

    public DetalleCompraResponseDTO(Integer idDetalleCompra, Integer idCompra, ProductoDTO producto, Integer cantidad, Double precioCompra) {
        this.idDetalleCompra = idDetalleCompra;
        this.idCompra = idCompra; // ¡Inicializado en constructor!
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
    }

    // Getters y Setters
    public Integer getIdDetalleCompra() { return idDetalleCompra; }
    public void setIdDetalleCompra(Integer idDetalleCompra) { this.idDetalleCompra = idDetalleCompra; }

    public Integer getIdCompra() { return idCompra; } // ¡Nuevo Getter!
    public void setIdCompra(Integer idCompra) { this.idCompra = idCompra; } // ¡Nuevo Setter!

    public ProductoDTO getProducto() { return producto; }
    public void setProducto(ProductoDTO producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }
}