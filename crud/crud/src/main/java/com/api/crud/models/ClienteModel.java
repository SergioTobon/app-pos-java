package com.api.crud.models;

import jakarta.persistence.*;

@Entity // Indica que esta clase es una entidad JPA y se mapea a una tabla de base de datos
@Table(name = "cliente") // Especifica el nombre de la tabla en la base de datos
public class ClienteModel {

    @Id // Marca el campo como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura la generación automática del ID
    @Column(name = "id_cliente") // Nombre de la columna en la base de datos
    private Integer idCliente;

    @Column(name = "dni", unique = true, nullable = false, length = 20) // DNI, único y no nulo
    private String dni;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "contacto", length = 255) // Información de contacto (ej. email, teléfono)
    private String contacto;

    // Constructor vacío
    public ClienteModel() {
    }

    // Constructor con parámetros
    public ClienteModel(Integer idCliente, String dni, String nombre, String contacto) {
        this.idCliente = idCliente;
        this.dni = dni;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    // Getters y Setters
    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
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
