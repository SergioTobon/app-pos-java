package com.api.crud.controllers;

import com.api.crud.models.UserModel;
import com.api.crud.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permite acceso desde el frontend
public class UserController {

    @Autowired
    private UserServices userServices;

    @GetMapping
    public List<UserModel> listarUsuarios() {
        return userServices.listarUsuarios();
    }

    @GetMapping("/buscarPorId/{id}")
    public Optional<UserModel> obtenerUsuario(@PathVariable Long id) {
        return userServices.obtenerUsuarioPorId(id);
    }

    @GetMapping("/buscarPorDni/{dni}")
    public Optional<UserModel> obtenerUsuarioPorDni(@PathVariable String dni) {
        return userServices.obtenerUsuarioPorDni(dni);
    }

    @PostMapping("/crear")
    public UserModel agregarUsuario(@RequestBody UserModel usuario) {
        return userServices.guardarUsuario(usuario);
    }

    @PutMapping("/editar/{id}")
    public UserModel actualizarUsuario(@PathVariable int id, @RequestBody UserModel usuario) {
        usuario.setIdUsuario(id);
        return userServices.guardarUsuario(usuario);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        return userServices.eliminarUsuario(id);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserModel usuario) {
        return userServices.loginUsuario(usuario.getDni(), usuario.getPassword());
    }

    @GetMapping("/all")
    public List<UserModel> obtenerUsuarios() {
        return userServices.obtenerTodosLosUsuarios(); // Llama al servicio
    }
}
