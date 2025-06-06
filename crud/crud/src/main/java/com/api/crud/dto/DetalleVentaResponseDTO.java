package com.api.crud.dto;

// Importa ProductoDTO ya que un detalle de venta incluye información del producto
import com.api.crud.dto.ProductoDTO;

public class DetalleVentaResponseDTO {

    private Integer idDetalleVenta;
    // private Integer idVenta; // No es estrictamente necesario si ya está anidado bajo VentaResponseDTO
    private ProductoDTO producto; // DTO anidado para el producto
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public DetalleVentaResponseDTO() {
    }

    public DetalleVentaResponseDTO(Integer idDetalleVenta, ProductoDTO producto, Integer cantidad, Double precioUnitario, Double subtotal) {
        this.idDetalleVenta = idDetalleVenta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public Integer getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(Integer idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public ProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
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

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }
}