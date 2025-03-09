package com.api.crud.controllers;

import com.api.crud.dto.UserDTO;
import com.api.crud.models.LoginRequestModel;
import com.api.crud.models.UserModel;
import com.api.crud.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestModel request) {
        Optional<UserModel> userModel = userServices.autenticar(request.getDni(), request.getPassword());

        if (userModel.isPresent()) {
            UserModel user = userModel.get();

            // Crear una respuesta en formato JSON con toda la información relevante
            Map<String, Object> response = new HashMap<>();
            response.put("id_usuarios", user.getIdUsuario()); // 🔹 Aquí agregamos el ID del usuario
            response.put("dni", user.getDni());
            response.put("nombre", user.getNombre());
            response.put("id_rol", user.getIdRol()); // Enviar el rol del usuario

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login fallido");
        }
    }


    @PostMapping("/crear")
    public ResponseEntity<UserModel> createUser(@RequestBody UserDTO userDTO) {
        UserModel user = userServices.createUser(userDTO);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long idUsuario) {
        userServices.deleteUser(idUsuario);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long idUsuario, @RequestBody UserDTO userDTO) {
        try {
            UserModel updatedUser = userServices.updateUser(idUsuario, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<UserModel>> getEmployees() {
        List<UserModel> employees = userServices.getUsersByIdRol(2);
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/administradores")
    public ResponseEntity<List<UserModel>> getadministradores() {
        List<UserModel> administradores = userServices.getUsersByIdRol(1);
        return ResponseEntity.ok(administradores);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> allUsers = userServices.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        Optional<UserModel> user = userServices.obtenerPorId(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

}
