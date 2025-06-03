package com.api.crud.models;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios") // Nombre de la tabla
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario") // Coincide con tu esquema
    private Integer idUsuario;

    @Column(name = "dni", unique = true, nullable = false, length = 20)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "contacto", length = 255)
    private String contacto; // Puede ser email o teléfono

    @Column(name = "password", nullable = false, length = 255) // Es crucial manejar contraseñas de forma segura (ej. encriptación)
    private String password;

    // Relación Many-to-One con RolModel
    @ManyToOne(fetch = FetchType.EAGER) // Un usuario tiene un rol
    @JoinColumn(name = "id_rol", nullable = false) // Columna FK en la tabla 'usuarios'
    private RolModel rol; // Campo que referencia al RolModel

    // Constructor vacío
    public UsuarioModel() {
    }

    // Constructor con parámetros (sin id por ser autogenerado)
    public UsuarioModel(Integer idUsuario, String dni, String nombre, String apellido, String contacto, String password, RolModel rol) {
        this.idUsuario = idUsuario;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.contacto = contacto;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters
    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
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

    public RolModel getRol() {
        return rol;
    }

    public void setRol(RolModel rol) {
        this.rol = rol;
    }
}