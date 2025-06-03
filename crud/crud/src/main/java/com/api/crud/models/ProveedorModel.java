
package com.api.crud.models;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "proveedores")
public class ProveedorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer id;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String nit;

    @Column(length = 50)
    private String contacto;

    @Column(length = 150)
    private String direccion;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 20)
    private String telefono;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CompraModel> compras;


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

    public List<CompraModel> getCompras() {
        return compras;
    }

    public void setCompras(List<CompraModel> compras) {
        this.compras = compras;
    }
}