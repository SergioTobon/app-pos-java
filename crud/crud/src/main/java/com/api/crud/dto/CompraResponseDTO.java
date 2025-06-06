package com.api.crud.dto;

import com.fasterxml.jackson.annotation.JsonFormat; // Para formatear la fecha
import java.time.LocalDateTime; // Usamos LocalDateTime como en el modelo
import java.util.List;

// Asumimos que ProveedorDTO ya tiene los getters y setters adecuados
// y que su campo 'id' es Integer.
import com.api.crud.dto.ProveedorDTO;

public class CompraResponseDTO {
    private Integer idCompra;
    private Double total;

    // Para asegurar que la fecha se formatee correctamente en la respuesta JSON
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;

    private ProveedorDTO proveedor; // Detalles del proveedor asociado a la compra
    private List<DetalleCompraResponseDTO> detalles; // Lista de detalles de la compra

    // Constructor vacío
    public CompraResponseDTO() {}

    // Constructor con parámetros
    public CompraResponseDTO(Integer idCompra, Double total, LocalDateTime fecha, ProveedorDTO proveedor, List<DetalleCompraResponseDTO> detalles) {
        this.idCompra = idCompra;
        this.total = total;
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.detalles = detalles;
    }

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

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public ProveedorDTO getProveedor() {
        return proveedor;
    }

    public void setProveedor(ProveedorDTO proveedor) {
        this.proveedor = proveedor;
    }

    public List<DetalleCompraResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleCompraResponseDTO> detalles) {
        this.detalles = detalles;
    }
}