package com.api.crud.controllers;

import com.api.crud.dto.ProductoDTO;
import com.api.crud.dto.ProductoUpdateDto;
import com.api.crud.dto.ProveedorAsociadoDto;
import com.api.crud.models.ProductoModel;
import com.api.crud.models.ProductoProveedorModel;
import com.api.crud.models.ProveedorModel;
import com.api.crud.services.ProductoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    // Body: { "nombre": "Laptop", "stock": 50, "descripcion": "PC portátil", "precioCompra": 800.0, "idProveedor": 1 }
    @PostMapping
    public ResponseEntity<ProductoDTO> guardarProducto(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.guardarProducto(productoDTO);
        // HttpStatus.CREATED (201) indica que el recurso fue creado exitosamente
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // PUT: Actualizar un producto existente
    // URL: PUT http://localhost:8080/api/productos/{id}

    @PutMapping(path = "/{id}")
    public ResponseEntity<ProductoModel> actualizarProducto(@PathVariable Integer id, @RequestBody ProductoUpdateDto productoDto) {
        // Necesitarás mapear el DTO a un ProductoModel para tu servicio,
        // o modificar el servicio para aceptar el DTO y manejar el mapeo internamente.
        // Para simplificar, ajustemos la firma del método del servicio:

        // Crea un ProductoModel temporal a partir del DTO para pasarlo al servicio
        ProductoModel tempProducto = new ProductoModel();
        tempProducto.setNombre(productoDto.getNombre());
        tempProducto.setDescripcion(productoDto.getDescripcion());
        tempProducto.setStock(productoDto.getStock());
        tempProducto.setPrecioVenta(productoDto.getPrecioVenta());

        // Convierte ProveedorAsociadoDto a ProductoProveedorModel para el servicio
        Set<ProductoProveedorModel> tempProductoProveedores = new HashSet<>();
        if (productoDto.getProveedoresAsociados() != null) {
            for (ProveedorAsociadoDto provDto : productoDto.getProveedoresAsociados()) {
                ProductoProveedorModel pp = new ProductoProveedorModel();
                // Para que el servicio obtenga el ProveedorModel, necesitas establecer su ID
                ProveedorModel tempProv = new ProveedorModel();
                tempProv.setId(provDto.getIdProveedor());
                pp.setProveedor(tempProv);
                pp.setPrecio(provDto.getPrecioCompraEspecifico());
                tempProductoProveedores.add(pp);
            }
        }
        tempProducto.setProductoProveedores(tempProductoProveedores); // Establece las asociaciones convertidas

        Optional<ProductoModel> producto = productoService.actualizarProducto(id, tempProducto);
        if (producto.isPresent()) {
            return new ResponseEntity<>(producto.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE: Eliminar un producto
    // URL: DELETE http://localhost:8080/api/productos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        boolean eliminado = productoService.eliminarProducto(id);
        if (eliminado) {
            // HttpStatus.NO_CONTENT (204) indica que la operación fue exitosa y no hay contenido para devolver
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
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // O HttpStatus.NOT_FOUND si prefieres
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
}