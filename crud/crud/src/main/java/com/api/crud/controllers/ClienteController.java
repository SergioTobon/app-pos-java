package com.api.crud.controllers;

import com.api.crud.dto.ClienteDTO;
import com.api.crud.services.ClienteServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes") // Define la ruta base para este controlador
public class ClienteController {

    @Autowired
    private ClienteServices clienteServices;

    // GET /api/clientes - Obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<ClienteDTO>> obtenerTodosLosClientes() {
        List<ClienteDTO> clientes = clienteServices.obtenerTodosLosClientes();
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    // GET /api/clientes/{id} - Obtener un cliente por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Integer id) {
        Optional<ClienteDTO> clienteOptional = clienteServices.obtenerClientePorId(id);
        return clienteOptional.map(cliente -> new ResponseEntity<>(cliente, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST /api/clientes - Crear un nuevo cliente
    @PostMapping
    public ResponseEntity<?> guardarCliente(@RequestBody ClienteDTO clienteDTO) {
        try {
            ClienteDTO nuevoCliente = clienteServices.guardarCliente(clienteDTO);
            return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Captura excepciones de validación (ej. DNI duplicado)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/clientes/{id} - Actualizar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(@PathVariable Integer id, @RequestBody ClienteDTO clienteDTO) {
        // Asegurarse de que el ID en el DTO coincida con el ID de la URL
        if (clienteDTO.getId() == null || !clienteDTO.getId().equals(id)) {
            clienteDTO.setId(id); // Forzar el ID del DTO a ser el de la URL
        }

        try {
            // Primero, verificar si el cliente existe
            if (clienteServices.obtenerClientePorId(id).isEmpty()) {
                return new ResponseEntity<>("Cliente no encontrado para actualizar", HttpStatus.NOT_FOUND);
            }
            ClienteDTO clienteActualizado = clienteServices.guardarCliente(clienteDTO);
            return new ResponseEntity<>(clienteActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/clientes/{id} - Eliminar un cliente por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCliente(@PathVariable Integer id) {
        boolean eliminado = clienteServices.eliminarCliente(id);
        if (eliminado) {
            return new ResponseEntity<>("Cliente eliminado exitosamente", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Cliente no encontrado", HttpStatus.NOT_FOUND);
        }
    }
}