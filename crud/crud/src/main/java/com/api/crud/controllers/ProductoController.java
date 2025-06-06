package com.api.crud.controllers;

import com.api.crud.dto.ProductoDTO; // Asegúrate de importar ProductoDTO
// import com.api.crud.dto.ProductoUpdateDto; // <-- YA NO NECESITAS ESTA IMPORTACIÓN si la eliminas
// import com.api.crud.dto.ProveedorAsociadoDto; // <-- YA NO NECESITAS ESTA IMPORTACIÓN si la eliminas
import com.api.crud.models.ProductoModel; // Todavía la necesitas si tu servicio devuelve ProductoModel (aunque lo cambiamos a DTO)
import com.api.crud.services.ProductoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/productos") // Define la URL base para todos los endpoints de este controlador
public class ProductoController {

    private final ProductoServices productoService;

    @Autowired // Inyección de dependencias del servicio
    public ProductoController(ProductoServices productoService) {
        this.productoService = productoService;
    }

    // GET: Obtener todos los productos
    // URL: GET http://localhost:8080/api/productos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> obtenerTodosLosProductos() {
        List<ProductoDTO> productos = productoService.obtenerTodosLosProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    // GET: Obtener un producto por ID
    // URL: GET http://localhost:8080/api/productos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerProductoPorId(@PathVariable Integer id) {
        Optional<ProductoDTO> producto = productoService.obtenerProductoPorId(id);
        return producto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST: Guardar un nuevo producto
    // URL: POST http://localhost:8080/api/productos
    @PostMapping
    public ResponseEntity<ProductoDTO> guardarProducto(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.guardarProducto(productoDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // PUT: Actualizar un producto existente
    // URL: PUT http://localhost:8080/api/productos/{id}
    @PutMapping(path = "/{id}")
    // CAMBIOS CLAVE AQUÍ:
    // 1. Recibe ProductoDTO en lugar de ProductoUpdateDto
    // 2. La respuesta del servicio 'actualizarProducto' ya es ProductoDTO (como lo cambiamos en el ProductoServices)
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable Integer id, @RequestBody ProductoDTO productoDTO) {
        // Llama directamente al servicio con el ProductoDTO
        // El servicio se encargará de cargar el ProductoModel existente
        // y aplicar los campos del DTO, incluyendo el porcentajeGanancia,
        // y luego recalcular precioVenta.
        ProductoDTO productoActualizado = productoService.actualizarProducto(id, productoDTO); // <-- CAMBIO AQUI

        if (productoActualizado != null) { // El servicio devuelve un DTO o lanza excepción si no encuentra
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } else {
            // Esto solo se ejecutaría si tu servicio 'actualizarProducto' devolviera Optional.empty()
            // Pero como lo cambiamos para lanzar una excepción si no encuentra,
            // esta parte quizás ya no sea necesaria dependiendo de la implementación exacta.
            // Si el servicio devuelve el DTO directamente y lanza una excepción,
            // esta línea se simplifica a 'return new ResponseEntity<>(productoActualizado, HttpStatus.OK);'
            // y la excepción sería manejada por un @ControllerAdvice global.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Fallback por si acaso
        }
    }


    // DELETE: Eliminar un producto
    // URL: DELETE http://localhost:8080/api/productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        boolean eliminado = productoService.eliminarProducto(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET: Buscar productos por nombre (usando el método personalizado del servicio)
    // URL: GET http://localhost:8080/api/productos/buscar?nombre=lap
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductoPorNombre(@RequestParam String nombre) {
        List<ProductoDTO> productos = productoService.buscarPorNombre(nombre);
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
}