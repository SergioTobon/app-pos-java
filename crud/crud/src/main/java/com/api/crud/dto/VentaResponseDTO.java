package com.api.crud.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VentaResponseDTO {

    private Integer idVenta;
    private LocalDateTime fecha;
    private Double total;
    private UsuarioDTO usuario; // DTO anidado para el usuario que realizó la venta
    private ClienteDTO cliente; // DTO anidado para el cliente al que se le hizo la venta
    private List<DetalleVentaResponseDTO> detalles; // Lista de los detalles de la venta

    public VentaResponseDTO() {
    }

    public VentaResponseDTO(Integer idVenta, LocalDateTime fecha, Double total, UsuarioDTO usuario, ClienteDTO cliente, List<DetalleVentaResponseDTO> detalles) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.total = total;
        this.usuario = usuario;
        this.cliente = cliente;
        this.detalles = detalles;
    }

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

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public List<DetalleVentaResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaResponseDTO> detalles) {
        this.detalles = detalles;
    }
}