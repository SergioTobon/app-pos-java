package com.api.crud.controllers;

import com.api.crud.dto.ProductoProveedorDTO;
import com.api.crud.services.ProductoProveedorServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/producto-proveedor")
public class ProductoProveedorController {

    private final ProductoProveedorServices productoProveedorService;

    public ProductoProveedorController(ProductoProveedorServices productoProveedorService) {
        this.productoProveedorService = productoProveedorService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductoProveedorDTO>> listarProductoProveedores() {
        return ResponseEntity.ok(productoProveedorService.listarProductoProveedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoProveedorDTO> obtenerProductoProveedor(@PathVariable Integer id) {
        ProductoProveedorDTO productoProveedor = productoProveedorService.obtenerProductoProveedor(id);
        return productoProveedor != null ? ResponseEntity.ok(productoProveedor) : ResponseEntity.notFound().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<ProductoProveedorDTO> crearProductoProveedor(@RequestBody ProductoProveedorDTO productoProveedorDTO) {
        return ResponseEntity.ok(productoProveedorService.guardarProductoProveedor(productoProveedorDTO));
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<ProductoProveedorDTO> actualizarProductoProveedor(@PathVariable Integer id, @RequestBody ProductoProveedorDTO productoProveedorDTO) {
        ProductoProveedorDTO actualizado = productoProveedorService.actualizarProductoProveedor(id, productoProveedorDTO);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarProductoProveedor(@PathVariable Integer id) {
        productoProveedorService.eliminarProductoProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
