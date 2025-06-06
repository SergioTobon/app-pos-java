package com.api.crud.models; // Manteniendo el paquete 'models'

import jakarta.persistence.*;

@Entity
@Table(name = "detalle_compra") // Asumiendo este nombre de tabla
public class DetalleCompraModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_compra")
    private Integer idDetalleCompra; // Manteniendo Integer

    @ManyToOne // FetchType.LAZY es una buena práctica aquí, pero lo dejo por defecto EAGER para tu nivel
    @JoinColumn(name = "id_compra")
    private CompraModel compra;

    @ManyToOne // FetchType.LAZY es una buena práctica aquí
    @JoinColumn(name = "id_productos") // Asumiendo este nombre de columna
    private ProductoModel producto;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_compra") // ¡IMPORTANTE! Asegúrate que la columna en tu DB sea 'precio_compra'
    private Double precioCompra; // ¡Campo cambiado a precioCompra!

    // Getters y Setters
    public Integer getIdDetalleCompra() { // Getter y Setter para idDetalleCompra
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(Integer idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public CompraModel getCompra() {
        return compra;
    }

    public void setCompra(CompraModel compra) {
        this.compra = compra;
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

    public Double getPrecioCompra() { // ¡Getter para precioCompra!
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) { // ¡Setter para precioCompra!
        this.precioCompra = precioCompra;
    }
}