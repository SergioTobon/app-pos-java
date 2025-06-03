package com.api.crud.models;

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_venta") // Nombre de la tabla
public class DetalleVentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta") // Coincide con tu esquema
    private Integer idDetalleVenta;

    // Relación Many-to-One con VentaModel
    @ManyToOne(fetch = FetchType.LAZY) // Un detalle pertenece a una venta
    @JoinColumn(name = "id_venta", nullable = false) // FK en la tabla 'detalle_venta'
    private VentaModel venta; // Campo que referencia a la VentaModel

    // Relación Many-to-One con ProductoModel
    @ManyToOne(fetch = FetchType.EAGER) // Un detalle se refiere a un producto
    @JoinColumn(name = "id_productos", nullable = false) // FK en la tabla 'detalle_venta'
    private ProductoModel producto; // Campo que referencia al ProductoModel

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario; // Precio del producto al momento de la venta

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    // Constructor vacío
    public DetalleVentaModel() {
    }

    // Constructor con parámetros
    public DetalleVentaModel(Integer idDetalleVenta, VentaModel venta, ProductoModel producto, Integer cantidad, Double precioUnitario, Double subtotal) {
        this.idDetalleVenta = idDetalleVenta;
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // Getters y Setters
    public Integer getIdDetalleVenta() {
        return idDetalleVenta;
    }

    public void setIdDetalleVenta(Integer idDetalleVenta) {
        this.idDetalleVenta = idDetalleVenta;
    }

    public VentaModel getVenta() {
        return venta;
    }

    public void setVenta(VentaModel venta) {
        this.venta = venta;
    }

    public ProductoModel getProducto() {
        return producto;
    }

    public void setProducto(ProductoModel producto) {
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