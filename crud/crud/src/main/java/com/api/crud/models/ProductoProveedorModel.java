package com.api.crud.models;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "producto_proveedor")
public class ProductoProveedorModel {

    @EmbeddedId
    private ProductoProveedorId id;

    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto")
    private ProductoModel producto;

    @ManyToOne
    @MapsId("idProveedor")
    @JoinColumn(name = "id_proveedor")
    private ProveedorModel proveedor;

    @Column(name = "precio_compra")
    private Double precio;

    public ProductoProveedorModel() {
        this.id = new ProductoProveedorId();
    }

    public ProductoProveedorModel(ProductoModel producto, ProveedorModel proveedor, Double precio) {
        this.producto = producto;
        this.proveedor = proveedor;
        this.precio = precio;
        this.id = new ProductoProveedorId(producto.getId(), proveedor.getId());
    }

    public ProductoProveedorId getId() {
        return id;
    }

    public void setId(ProductoProveedorId id) {
        this.id = id;
    }

    public ProductoModel getProducto() {
        return producto;
    }

    public void setProducto(ProductoModel producto) {
        this.producto = producto;
        if (this.id == null) {
            this.id = new ProductoProveedorId();
        }
        this.id.setIdProducto(producto != null ? producto.getId() : null);
    }

    public ProveedorModel getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorModel proveedor) {
        this.proveedor = proveedor;
        if (this.id == null) {
            this.id = new ProductoProveedorId();
        }
        this.id.setIdProveedor(proveedor != null ? proveedor.getId() : null);
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoProveedorModel that = (ProductoProveedorModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}