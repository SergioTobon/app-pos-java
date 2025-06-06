package com.api.crud.models;

import jakarta.persistence.*;

@Entity
@Table(name = "roles") // Nombre de la tabla en la base de datos
public class RolModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_roles") // Coincide con tu esquema
    private Integer idRoles;

    @Column(name = "nombre", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // Constructor vacío
    public RolModel() {
    }

    // Constructor con parámetros
    public RolModel(Integer idRoles, String nombre, String descripcion) {
        this.idRoles = idRoles;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public Integer getIdRoles() {
        return idRoles;
    }

    public void setIdRoles(Integer idRoles) {
        this.idRoles = idRoles;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}