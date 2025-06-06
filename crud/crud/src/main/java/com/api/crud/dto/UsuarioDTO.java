package com.api.crud.dto;

public class UsuarioDTO {

    private Integer id;
    private String dni;
    private String nombre;
    private String apellido;
    private String contacto;
    // No incluir la contraseña (password) en el DTO de respuesta por seguridad
    private RolDTO rol; // DTO anidado para el rol

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer id, String dni, String nombre, String apellido, String contacto, RolDTO rol) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.contacto = contacto;
        this.rol = rol;
    }

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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public RolDTO getRol() {
        return rol;
    }

    public void setRol(RolDTO rol) {
        this.rol = rol;
    }
}