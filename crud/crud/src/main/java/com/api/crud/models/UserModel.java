package com.api.crud.models;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")

public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int idUsuario;

    @Column
    public String dni;
    public String nombre;
    public String apellido;
    public String contacto;
    public String password;

    //getters and setters

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
}
