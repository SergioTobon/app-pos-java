package com.api.crud.controllers;

import com.api.crud.dto.ProveedorDTO;
import com.api.crud.services.ProveedorServices; // ¡Importante: con 's' al final!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proveedores") // URL base para los proveedores
public class ProveedorController {

    private final ProveedorServices proveedorServices; // ¡Importante: con 's' al final!

    @Autowired
    public ProveedorController(ProveedorServices proveedorServices) { // Inyección con el nombre correcto
        this.proveedorServices = proveedorServices;
    }

    // GET: Obtener todos los proveedores
    // URL: GET http://localhost:8080/api/proveedores
    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> obtenerTodosLosProveedores() {
        List<ProveedorDTO> proveedores = proveedorServices.obtenerTodosLosProveedores();
        return new ResponseEntity<>(proveedores, HttpStatus.OK);
    }

    // GET: Obtener un proveedor por ID
    // URL: GET http://localhost:8080/api/proveedores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> obtenerProveedorPorId(@PathVariable Integer id) {
        Optional<ProveedorDTO> proveedor = proveedorServices.obtenerProveedorPorId(id);
        return proveedor.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST: Guardar un nuevo proveedor
    // URL: POST http://localhost:8080/api/proveedores
    // Body: { "nombre": "Proveedor A", "nit": "900123456-7", "contacto": "Juan Perez", "direccion": "Calle Falsa 123", "email": "contacto@proveedora.com", "telefono": "3101234567" }
    @PostMapping
    public ResponseEntity<ProveedorDTO> guardarProveedor(@RequestBody ProveedorDTO proveedorDTO) {
        ProveedorDTO nuevoProveedor = proveedorServices.guardarProveedor(proveedorDTO);
        return new ResponseEntity<>(nuevoProveedor, HttpStatus.CREATED);
    }

    // PUT: Actualizar un proveedor existente
    // URL: PUT http://localhost:8080/api/proveedores/{id}
    // Body: { "nombre": "Proveedor Actualizado", "nit": "900123456-7", "contacto": "Maria Lopez", "direccion": "Avenida Siempre Viva 742", "email": "info@proveedoractualizado.com", "telefono": "3119876543" }
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizarProveedor(@PathVariable Integer id, @RequestBody ProveedorDTO proveedorDTO) {
        Optional<ProveedorDTO> proveedorActualizado = proveedorServices.actualizarProveedor(id, proveedorDTO);
        return proveedorActualizado.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE: Eliminar un proveedor
    // URL: DELETE http://localhost:8080/api/proveedores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Integer id) {
        boolean eliminado = proveedorServices.eliminarProveedor(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}