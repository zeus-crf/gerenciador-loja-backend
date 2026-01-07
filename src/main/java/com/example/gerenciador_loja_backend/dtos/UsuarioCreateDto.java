package com.example.gerenciador_loja_backend.dtos;
public class UsuarioCreateDto {

    private String username;
    private String password;

    public UsuarioCreateDto() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
