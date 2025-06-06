// src/main/java/com/api/crud/controllers/ProductoProveedorController.java (EJEMPLO)

package com.api.crud.controllers;

import com.api.crud.dto.ProductoProveedorDTO;
import com.api.crud.services.ProductoProveedorServices; // Asegúrate de importar tu servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/producto-proveedor")
public class ProductoProveedorController {

    private final ProductoProveedorServices productoProveedorServices;

    @Autowired
    public ProductoProveedorController(ProductoProveedorServices productoProveedorServices) {
        this.productoProveedorServices = productoProveedorServices;
    }

    @GetMapping
    public ResponseEntity<List<ProductoProveedorDTO>> getAllProductoProveedores() {
        List<ProductoProveedorDTO> productoProveedores = productoProveedorServices.listarProductoProveedores();
        return new ResponseEntity<>(productoProveedores, HttpStatus.OK);
    }

    // --- CORRECCIÓN AQUÍ ---
    // El endpoint para obtener uno por IDs. Los IDs vienen como Path Variables.
    // Ejemplo: GET /producto-proveedor/1/2 (productoId=1, proveedorId=2)
    @GetMapping("/{idProducto}/{idProveedor}")
    public ResponseEntity<ProductoProveedorDTO> getProductoProveedorByIds(@PathVariable Integer idProducto, @PathVariable Integer idProveedor) {
        Optional<ProductoProveedorDTO> ppDTO = productoProveedorServices.obtenerProductoProveedorPorIds(idProducto, idProveedor);
        return ppDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ProductoProveedorDTO> createProductoProveedor(@RequestBody ProductoProveedorDTO productoProveedorDTO) {
        ProductoProveedorDTO newPp = productoProveedorServices.guardarProductoProveedor(productoProveedorDTO);
        return new ResponseEntity<>(newPp, HttpStatus.CREATED);
    }

    // --- CORRECCIÓN AQUÍ ---
    // El endpoint para actualizar uno por IDs. Los IDs vienen como Path Variables.
    // Ejemplo: PUT /producto-proveedor/1/2
    @PutMapping("/{idProducto}/{idProveedor}")
    public ResponseEntity<ProductoProveedorDTO> updateProductoProveedor(
            @PathVariable Integer idProducto,
            @PathVariable Integer idProveedor,
            @RequestBody ProductoProveedorDTO productoProveedorDTO) {
        ProductoProveedorDTO updatedPp = productoProveedorServices.actualizarProductoProveedor(idProducto, idProveedor, productoProveedorDTO);
        return updatedPp != null ?
                new ResponseEntity<>(updatedPp, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // --- CORRECCIÓN AQUÍ ---
    // El endpoint para eliminar uno por IDs. Los IDs vienen como Path Variables.
    // Ejemplo: DELETE /producto-proveedor/1/2
    @DeleteMapping("/{idProducto}/{idProveedor}")
    public ResponseEntity<Void> deleteProductoProveedor(@PathVariable Integer idProducto, @PathVariable Integer idProveedor) {
        productoProveedorServices.eliminarProductoProveedor(idProducto, idProveedor);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}