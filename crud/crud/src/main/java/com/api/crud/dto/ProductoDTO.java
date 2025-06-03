package com.api.crud.dto;

public class ProductoDTO {
    private Integer id; // Manteniendo Integer según tu preferencia
    private String nombre;
    private Integer stock;
    private String descripcion;
    private Double precioCompra; // Renombrado a precioCompra para consistencia con tu modelo
    private Double precioVenta; // Mantenemos precioVenta si es un campo que necesitas en la lógica de negocio o en la vista.
    // Si no lo vas a usar, puedes eliminarlo.
    private Integer idProveedor; // Manteniendo Integer
    private String nombreProveedor; // Útil para mostrar el nombre del proveedor en las respuestas

    // Constructor vacío (necesario para la deserialización JSON)
    public ProductoDTO() {}

    // Constructor para la creación/actualización de productos (petición de entrada)
    // No incluye 'id' (porque lo asigna la DB) ni 'nombreProveedor' (porque es un dato de salida)
    public ProductoDTO(String nombre, Integer stock, String descripcion, Double precioCompra, Integer idProveedor) {
        this.nombre = nombre;
        this.stock = stock;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.idProveedor = idProveedor;
    }

    // Constructor para la respuesta de una consulta de producto (salida)
    // Incluye todos los datos relevantes para la visualización o el consumo de la API.
    public ProductoDTO(Integer id, String nombre, Integer stock, String descripcion, Double precioCompra, Double precioVenta, Integer idProveedor, String nombreProveedor) {
        this.id = id;
        this.nombre = nombre;
        this.stock = stock;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
    }

    // Getters y Setters

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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }


    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public Double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(Double precioCompra) {
        this.precioCompra = precioCompra;
    }
}
