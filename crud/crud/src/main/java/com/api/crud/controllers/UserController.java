package com.api.crud.controllers;

import com.api.crud.models.LoginRequestModel;
import com.api.crud.models.UserModel;
import com.api.crud.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestModel request) {
        Optional<UserModel> userModel = userServices.autenticar(request.getDni(), request.getPassword());
        return userModel.isPresent() ?
                ResponseEntity.ok("Login exitoso") :
                ResponseEntity.status(401).body("Login fallido");
    }
}
