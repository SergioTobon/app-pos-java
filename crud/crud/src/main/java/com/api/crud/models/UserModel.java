package com.api.crud.models;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // Asegurar que coincide con la BD
    private int idUsuario;

    @Column(name = "dni")
    private String dni;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "contacto")
    private String contacto;

    @Column(name = "password")
    private String password;

    @Column(name = "id_rol") // Se mantiene la anotación si el nombre en la BD es id_rol
    private int idRol;  // Cambiado de id_rol a idRol para seguir convención de Java

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIdRol() {  // Cambiado de getId_rol() a getIdRol()
        return idRol;
    }

    public void setIdRol(int idRol) {  // Cambiado de setId_rol() a setIdRol()
        this.idRol = idRol;
    }
}
