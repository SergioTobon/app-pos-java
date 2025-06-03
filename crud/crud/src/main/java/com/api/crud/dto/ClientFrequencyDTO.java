// src/main/java/com/api/crud/dto/dashboard/ClientFrequencyDTO.java
package com.api.crud.dto;

public class ClientFrequencyDTO {
    private String nombreCliente;
    private Long numCompras; // O Double montoGastado, elige la métrica que prefieras

    public ClientFrequencyDTO(String nombreCliente, Long numCompras) {
        this.nombreCliente = nombreCliente;
        this.numCompras = numCompras;
    }

    // Getters y Setters
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Long getNumCompras() {
        return numCompras;
    }

    public void setNumCompras(Long numCompras) {
        this.numCompras = numCompras;
    }
}