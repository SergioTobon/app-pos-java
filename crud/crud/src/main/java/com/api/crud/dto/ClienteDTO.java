package com.api.crud.dto;

// No se necesitan anotaciones JPA aquí, solo campos simples
public class ClienteDTO {

    private Integer id;
    private String dni;
    private String nombre;
    private String contacto;

    // Constructor vacío
    public ClienteDTO() {
    }

    // Constructor con parámetros
    public ClienteDTO(Integer id, String dni, String nombre, String contacto) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }
}