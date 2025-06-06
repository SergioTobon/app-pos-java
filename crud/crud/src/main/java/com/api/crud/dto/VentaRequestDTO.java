package com.api.crud.dto;

import java.util.List;

public class VentaRequestDTO {

    private Integer idUsuario;
    private Integer idCliente;
    private List<DetalleVentaRequestDTO> productos; // Lista de los detalles de la venta

    public VentaRequestDTO() {
    }

    public VentaRequestDTO(Integer idUsuario, Integer idCliente, List<DetalleVentaRequestDTO> productos) {
        this.idUsuario = idUsuario;
        this.idCliente = idCliente;
        this.productos = productos;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public List<DetalleVentaRequestDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<DetalleVentaRequestDTO> productos) {
        this.productos = productos;
    }
}