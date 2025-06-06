package com.api.crud.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productos")
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_productos")
    private Integer id;

    @Column(nullable = false, length = 100, name = "nombre")
    private String nombre;

    @Column(nullable = false, name = "stock")
    private Integer stock;

    @Column(length = 255, name = "descripcion")
    private String descripcion;


    @Column(name = "precio_venta")
    private Double precioVenta;
    @Column(name = "precio_compra")
    private Double precioCompra;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductoProveedorModel> productoProveedores = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }


    public Set<ProductoProveedorModel> getProductoProveedores() {
        return productoProveedores;
    }

    public void setProductoProveedores(Set<ProductoProveedorModel> productoProveedores) {
        this.productoProveedores = productoProveedores;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public void addProveedor(ProveedorModel proveedor, Double precioCompra) {
        ProductoProveedorModel productoProveedor = new ProductoProveedorModel(this, proveedor, precioCompra);
        this.productoProveedores.add(productoProveedor);

        proveedor.getProductoProveedores().add(productoProveedor);
    }

    public void removeProveedor(ProveedorModel proveedor) {

        ProductoProveedorModel productoProveedorToRemove = this.productoProveedores.stream()
                .filter(pp -> pp.getProveedor().equals(proveedor))
                .findFirst()
                .orElse(null);

        if (productoProveedorToRemove != null) {
            this.productoProveedores.remove(productoProveedorToRemove);

            proveedor.getProductoProveedores().remove(productoProveedorToRemove);

        }
    }
}