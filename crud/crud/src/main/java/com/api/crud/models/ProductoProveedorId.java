// src/main/java/com/api/crud/models/ProductoProveedorId.java
package com.api.crud.models;

// Ejemplo de ProductoProveedorId (si lo tienes como clase separada)
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductoProveedorId implements Serializable {
    private Integer idProducto;
    private Integer idProveedor;

    // Constructores
    public ProductoProveedorId() {}
    public ProductoProveedorId(Integer idProducto, Integer idProveedor) {
        this.idProducto = idProducto;
        this.idProveedor = idProveedor;
    }

    // Getters y Setters
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }

    // IMPORTANTE: Implementación de equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductoProveedorId that = (ProductoProveedorId) o;
        return Objects.equals(idProducto, that.idProducto) &&
                Objects.equals(idProveedor, that.idProveedor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProducto, idProveedor);
    }
}