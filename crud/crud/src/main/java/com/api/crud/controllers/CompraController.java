package com.api.crud.controllers;

import com.api.crud.dto.CompraRequestDTO; // DTO para la solicitud de creación de compra
import com.api.crud.dto.CompraResponseDTO; // DTO para la respuesta de compra
import com.api.crud.services.CompraServices; // ¡Importante: con 's' al final!
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/compras") // URL base para las compras
public class CompraController {

    private final CompraServices compraServices; // ¡Importante: con 's' al final!

    @Autowired
    public CompraController(CompraServices compraServices) { // Inyección con el nombre correcto
        this.compraServices = compraServices;
    }

    // GET: Obtener todas las compras
    // URL: GET http://localhost:8080/api/compras
    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> obtenerTodasLasCompras() {
        List<CompraResponseDTO> compras = compraServices.obtenerTodasLasCompras();
        return new ResponseEntity<>(compras, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerCompraPorId(@PathVariable Integer id) {
        Optional<CompraResponseDTO> compra = compraServices.obtenerCompraPorId(id);
        return compra.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<CompraResponseDTO> guardarCompra(@RequestBody CompraRequestDTO compraRequestDTO) {
        try {
            CompraResponseDTO nuevaCompra = compraServices.guardarCompra(compraRequestDTO);
            if (nuevaCompra == null) {
                // Esto podría ocurrir si el servicio devuelve null por un error validado internamente
                // (ej. proveedor no encontrado, lista de productos vacía)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(nuevaCompra, HttpStatus.CREATED);
        } catch (RuntimeException e) {

            System.err.println("Error al guardar la compra: " + e.getMessage());

            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


}