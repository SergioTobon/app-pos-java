package com.api.crud.dto;

public class LoginRequestDTO {
    private String dni;
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String dni, String password) {
        this.dni = dni;
        this.password = password;
    }

    // Getters y Setters
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}