package com.api.crud.models; // Manteniendo el paquete 'models'

import com.fasterxml.jackson.annotation.JsonManagedReference; // Si lo estás usando para la relación con DetalleCompra
import jakarta.persistence.*;
import java.time.LocalDateTime; // ¡IMPORTANTE! Usar LocalDateTime
import java.util.List;

@Entity
@Table(name = "compra") // Asumiendo este nombre de tabla
public class CompraModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra; // Manteniendo Integer

    @Column(name = "total")
    private Double total;

    @Column(name = "fecha")
    private LocalDateTime fecha; // ¡IMPORTANTE! Campo cambiado a LocalDateTime

    // Relación con detalle_compra
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true) // Añadido orphanRemoval=true (opcional, pero buena práctica)
    @JsonManagedReference // Necesario si DetalleCompraModel tiene @JsonBackReference
    private List<DetalleCompraModel> detalles;

    @ManyToOne // FetchType.LAZY es una buena práctica aquí
    @JoinColumn(name = "id_proveedor") // clave foránea en la tabla compra
    // @JsonBackReference // Necesario si ProveedorModel tiene @JsonManagedReference sobre compras
    private ProveedorModel proveedor;

    // Getters y Setters
    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFecha() { // ¡Getter para LocalDateTime!
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) { // ¡Setter para LocalDateTime!
        this.fecha = fecha;
    }

    public List<DetalleCompraModel> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraModel> detalles) {
        this.detalles = detalles;
    }

    public ProveedorModel getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorModel proveedor) {
        this.proveedor = proveedor;
    }
}