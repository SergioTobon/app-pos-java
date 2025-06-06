package com.api.crud.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List; // Para la relación One-to-Many

@Entity
@Table(name = "venta") // Nombre de la tabla
public class VentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta") // Coincide con tu esquema
    private Integer idVenta;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha; // Usamos LocalDateTime para fecha y hora

    @Column(name = "total", nullable = false)
    private Double total;

    // Relación Many-to-One con UsuarioModel (quién realizó la venta)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false) // FK en la tabla 'venta'
    private UsuarioModel usuario;

    // Relación Many-to-One con ClienteModel (a quién se le hizo la venta)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false) // FK en la tabla 'venta'
    private ClienteModel cliente;

    // Relación One-to-Many con DetalleVentaModel
    // cascade = CascadeType.ALL: Si borras una venta, sus detalles se borran.
    // orphanRemoval = true: Si eliminas un detalle de la lista, se borra de la DB.
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleVentaModel> detalles; // Lista de detalles asociados a esta venta

    // Constructor vacío
    public VentaModel() {
        this.fecha = LocalDateTime.now(); // Inicializa la fecha al momento de la creación
    }

    // Constructor con parámetros
    public VentaModel(Integer idVenta, LocalDateTime fecha, Double total, UsuarioModel usuario, ClienteModel cliente, List<DetalleVentaModel> detalles) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.usuario = usuario;
        this.cliente = cliente;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }

    public ClienteModel getCliente() {
        return cliente;
    }

    public void setCliente(ClienteModel cliente) {
        this.cliente = cliente;
    }

    public List<DetalleVentaModel> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaModel> detalles) {
        this.detalles = detalles;
    }

    // Helper para añadir detalles (opcional pero útil)
    public void addDetalle(DetalleVentaModel detalle) {
        if (detalles == null) {
            detalles = new java.util.ArrayList<>();
        }
        detalles.add(detalle);
        detalle.setVenta(this);
    }
}