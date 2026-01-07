package com.example.gerenciador_loja_backend.dtos;

import java.time.LocalDate;

public class ClienteFilterDto {
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private LocalDate dataInicial;
    private LocalDate dataFinal;
    private String ordenacao; // "RECENTE" ou "ANTIGO"

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public LocalDate getDataInicial() { return dataInicial; }
    public void setDataInicial(LocalDate dataInicial) { this.dataInicial = dataInicial; }

    public LocalDate getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDate dataFinal) { this.dataFinal = dataFinal; }

    public String getOrdenacao() { return ordenacao; }
    public void setOrdenacao(String ordenacao) { this.ordenacao = ordenacao; }
}
