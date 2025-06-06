package com.api.crud.dto;

public class LoginDTO {
    private String dni;
    private String password;

    public LoginDTO() {}

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
