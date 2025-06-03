package com.api.crud.controllers;

import com.api.crud.dto.VentaRequestDTO;
import com.api.crud.dto.VentaResponseDTO;
import com.api.crud.services.VentaServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas") // Define la ruta base para este controlador
public class VentaController {

    @Autowired
    private VentaServices ventaServices;

    // POST /api/ventas - Crear una nueva venta
    @PostMapping
    public ResponseEntity<?> guardarVenta(@RequestBody VentaRequestDTO ventaRequestDTO) {
        try {
            VentaResponseDTO nuevaVenta = ventaServices.guardarVenta(ventaRequestDTO);
            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Captura excepciones de validación (ej. producto no encontrado, stock insuficiente)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/ventas - Obtener todas las ventas
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> obtenerTodasLasVentas() {
        List<VentaResponseDTO> ventas = ventaServices.obtenerTodasLasVentas();
        return new ResponseEntity<>(ventas, HttpStatus.OK);
    }

    // GET /api/ventas/{id} - Obtener una venta por su ID
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Integer id) {
        Optional<VentaResponseDTO> ventaOptional = ventaServices.obtenerVentaPorId(id);
        return ventaOptional.map(venta -> new ResponseEntity<>(venta, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Para un sistema de ventas real, no es común tener PUT o DELETE directos
    // para las ventas completas. Se suelen manejar con estados (ej. ANULADA, DEVUELTA)
    // o con documentos de anulación/devolución para mantener la trazabilidad.
    // Por simplicidad, no los implementamos aquí, pero es una consideración importante.
}