package com.api.crud.controllers;

import com.api.crud.dto.LoginRequestDTO;
import com.api.crud.dto.UsuarioDTO;
import com.api.crud.services.UsuarioServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Define una clase interna para la respuesta de error JSON
// Ojo: Si ya tienes una clase global para errores (ej. un GlobalExceptionHandler), puedes usarla
class ErrorResponse {
    private String status;
    private String message;

    public ErrorResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters
    public String getStatus() { return status; }
    public String getMessage() { return message; }
}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioServices usuarioServices;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getDni(),
                            loginRequest.getPassword()
                    )
            );

            String authenticatedDni = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
            UsuarioDTO authenticatedUserDTO = usuarioServices.obtenerUsuarioPorDni(authenticatedDni)
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado en DB con DNI: " + authenticatedDni));

            return new ResponseEntity<>(authenticatedUserDTO, HttpStatus.OK);

        } catch (AuthenticationException e) {
            // *** CAMBIO AQUÍ: Devuelve un objeto JSON para el error 401 ***
            return new ResponseEntity<>(new ErrorResponse("Unauthorized", "Credenciales inválidas: " + e.getMessage()), HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            // *** CAMBIO AQUÍ: Devuelve un objeto JSON para otros errores ***
            return new ResponseEntity<>(new ErrorResponse("Internal Server Error", "Error interno al procesar login: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}