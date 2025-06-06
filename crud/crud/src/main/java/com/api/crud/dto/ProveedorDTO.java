package com.api.crud.dto; // Mantenido en minúsculas

public class ProveedorDTO {
    private Integer id; // Mantenido como Integer
    private String nombre;
    private String nit;
    private String contacto;
    private String direccion;
    private String email;
    private String telefono;

    // Constructor vacío (necesario para la deserialización JSON)
    public ProveedorDTO() {}

    // Constructor para respuestas (GET) y, opcionalmente, para actualizar (PUT) si el ID viene en el cuerpo
    public ProveedorDTO(Integer id, String nombre, String nit, String contacto, String direccion, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.nit = nit;
        this.contacto = contacto;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
    }

    // Opcional: Constructor para crear (POST)
    // No incluye 'id' porque lo asigna la base de datos
    public ProveedorDTO(String nombre, String nit, String contacto, String direccion, String email, String telefono) {
        this.nombre = nombre;
        this.nit = nit;
        this.contacto = contacto;
        this.direccion = direccion;
        this.email = email;
        this.telefono = telefono;
    }


    // Getters y Setters (manteniendo la notación explícita)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}