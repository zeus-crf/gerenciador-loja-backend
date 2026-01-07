package com.example.gerenciador_loja_backend.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;


public record ClienteFiltroRequest(
        String nome,
        String email,
        String telefone,
        String endereco,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataInicial,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataFinal
) {}