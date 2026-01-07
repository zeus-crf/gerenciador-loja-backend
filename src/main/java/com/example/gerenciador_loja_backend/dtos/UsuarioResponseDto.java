package com.example.gerenciador_loja_backend.dtos;

import java.util.UUID;

public class UsuarioResponseDto {

    private UUID id;
    private String username;

    public UsuarioResponseDto(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
