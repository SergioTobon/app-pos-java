package com.api.crud.controllers;

import com.api.crud.dto.RolDTO;
import com.api.crud.services.RolServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolServices rolServices;

    // GET /api/roles - Obtener todos los roles
    @GetMapping
    public ResponseEntity<List<RolDTO>> obtenerTodosLosRoles() {
        List<RolDTO> roles = rolServices.obtenerTodosLosRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    // GET /api/roles/{id} - Obtener un rol por su ID
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> obtenerRolPorId(@PathVariable Integer id) {
        Optional<RolDTO> rolOptional = rolServices.obtenerRolPorId(id);
        return rolOptional.map(rol -> new ResponseEntity<>(rol, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST /api/roles - Crear un nuevo rol
    @PostMapping
    public ResponseEntity<?> guardarRol(@RequestBody RolDTO rolDTO) {
        try {
            RolDTO nuevoRol = rolServices.guardarRol(rolDTO);
            return new ResponseEntity<>(nuevoRol, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/roles/{id} - Actualizar un rol existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @RequestBody RolDTO rolDTO) {
        if (rolDTO.getId() == null || !rolDTO.getId().equals(id)) {
            rolDTO.setId(id);
        }
        try {
            if (rolServices.obtenerRolPorId(id).isEmpty()) {
                return new ResponseEntity<>("Rol no encontrado para actualizar", HttpStatus.NOT_FOUND);
            }
            RolDTO rolActualizado = rolServices.guardarRol(rolDTO);
            return new ResponseEntity<>(rolActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // DELETE /api/roles/{id} - Eliminar un rol por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRol(@PathVariable Integer id) {
        boolean eliminado = rolServices.eliminarRol(id);
        if (eliminado) {
            return new ResponseEntity<>("Rol eliminado exitosamente", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Rol no encontrado", HttpStatus.NOT_FOUND);
        }
    }
}