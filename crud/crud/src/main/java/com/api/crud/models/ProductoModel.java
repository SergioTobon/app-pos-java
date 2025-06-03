package com.api.crud.models;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.List;
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

    @Column(name = "precio_compra")
    private Double precioCompra;

    @Column(name = "precio_venta") // Nueva columna para el precio de venta
    private Double precioVenta;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", referencedColumnName = "id_proveedor")
    private ProveedorModel proveedor;


    //@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    //private Set<ProductoProveedorModel> productoProveedoresModel;


    // Getters y Setters

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

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }

    public ProveedorModel getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorModel proveedor) {
        this.proveedor = proveedor;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }
}


