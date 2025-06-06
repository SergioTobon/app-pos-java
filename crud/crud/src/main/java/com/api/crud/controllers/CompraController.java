package com.api.crud.controllers;

import com.api.crud.dto.CompraRequestDTO; // DTO para la solicitud de creación de compra
import com.api.crud.dto.CompraResponseDTO; // DTO para la respuesta de compra
import com.api.crud.dto.DetalleCompraRequestDTO;
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

    // GET: Obtener compra por ID
    // URL: GET http://localhost:8080/api/compras/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtenerCompraPorId(@PathVariable Integer id) {
        Optional<CompraResponseDTO> compra = compraServices.obtenerCompraPorId(id);
        return compra.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST: Guardar una nueva compra
    // URL: POST http://localhost:8080/api/compras
    @PostMapping
    public ResponseEntity<CompraResponseDTO> guardarCompra(@RequestBody CompraRequestDTO compraRequestDTO) {
        // --- LÍNEAS DE DEPURACIÓN AGREGADAS TEMPORALMENTE ---
        System.out.println("JSON de Compra recibido en el controlador:");
        System.out.println("ID de Proveedor: " + compraRequestDTO.getIdProveedor());
        if (compraRequestDTO.getProductos() != null) {
            System.out.println("Número de productos en la lista: " + compraRequestDTO.getProductos().size());
            for (int i = 0; i < compraRequestDTO.getProductos().size(); i++) {
                DetalleCompraRequestDTO detalle = compraRequestDTO.getProductos().get(i);
                System.out.println("  Producto #" + (i + 1) + ":");
                System.out.println("    ID Producto: " + detalle.getIdProducto());
                System.out.println("    Cantidad: " + detalle.getCantidad());
                System.out.println("    Precio Unitario Compra: " + detalle.getPrecioCompra());
            }
        } else {
            System.out.println("La lista de productos es NULL.");
        }
        // --- FIN DE LAS LÍNEAS DE DEPURACIÓN ---

        try {
            CompraResponseDTO nuevaCompra = compraServices.guardarCompra(compraRequestDTO);
            // Si el servicio no lanza excepción sino que retorna null para errores,
            // esta verificación es útil. Sin embargo, lanzar excepciones es más robusto.
            if (nuevaCompra == null) {
                // Si el servicio lanza excepciones específicas como IllegalArgumentException,
                // este bloque podría no ser necesario o ser más específico.
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(nuevaCompra, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Captura errores de argumentos inválidos (ej. lista de productos vacía, precio negativo)
            System.err.println("Error de validación al guardar la compra: " + e.getMessage());
            // HttpStatus.BAD_REQUEST (400) es apropiado para errores del cliente
            // Considera devolver el mensaje de error en el cuerpo de la respuesta para el cliente.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Captura otros errores de tiempo de ejecución (ej. producto/proveedor no encontrado)
            System.err.println("Error interno al guardar la compra: " + e.getMessage());
            // HttpStatus.INTERNAL_SERVER_ERROR (500) para errores inesperados del servidor
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}