package com.example.gerenciador_loja_backend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteDto(
        UUID id,
        String nome,
        String telefone,
        String email,
        String endereco,
        String notas,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {}
