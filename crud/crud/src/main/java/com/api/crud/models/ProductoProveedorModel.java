package com.api.crud.models;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_proveedor")
public class ProductoProveedorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto_proveedor")
    private Integer id;

    @Column(name = "id_productos")
    private Integer idProductos;

    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_productos", nullable = false, insertable = false, updatable = false)
    private ProductoModel producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", insertable = false, updatable = false, nullable = false)
    private ProveedorModel proveedor;

    @Column(nullable = false)
    private Double precio;

    // Getters y Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductoModel getProducto() {
        return producto;
    }

    public void setProducto(ProductoModel producto) {
        this.producto = producto;
    }

    public ProveedorModel getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorModel proveedor) {
        this.proveedor = proveedor;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getIdProductos() {
        return idProductos;
    }

    public void setIdProductos(Integer idProductos) {
        this.idProductos = idProductos;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
}