// src/main/java/com/api/crud/dto/dashboard/DashboardMetricsDTO.java
package com.api.crud.dto;

public class DashboardMetricsDTO {
    private Long totalProductos;
    private Long totalClientes;
    private Long numVentasUltimos30Dias;
    private Double totalComprasUltimos30Dias; // Usamos Double para montos

    // Constructor vacío
    public DashboardMetricsDTO() {
    }

    // Constructor con todos los campos
    public DashboardMetricsDTO(Long totalProductos, Long totalClientes, Long numVentasUltimos30Dias, Double totalComprasUltimos30Dias) {
        this.totalProductos = totalProductos;
        this.totalClientes = totalClientes;
        this.numVentasUltimos30Dias = numVentasUltimos30Dias;
        this.totalComprasUltimos30Dias = totalComprasUltimos30Dias;
    }

    // Getters y Setters
    public Long getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(Long totalProductos) {
        this.totalProductos = totalProductos;
    }

    public Long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(Long totalClientes) {
        this.totalClientes = totalClientes;
    }

    public Long getNumVentasUltimos30Dias() {
        return numVentasUltimos30Dias;
    }

    public void setNumVentasUltimos30Dias(Long numVentasUltimos30Dias) {
        this.numVentasUltimos30Dias = numVentasUltimos30Dias;
    }

    public Double getTotalComprasUltimos30Dias() {
        return totalComprasUltimos30Dias;
    }

    public void setTotalComprasUltimos30Dias(Double totalComprasUltimos30Dias) {
        this.totalComprasUltimos30Dias = totalComprasUltimos30Dias;
    }
}