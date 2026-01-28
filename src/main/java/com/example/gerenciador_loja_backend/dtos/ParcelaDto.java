package com.example.gerenciador_loja_backend.dtos;

import com.example.gerenciador_loja_backend.enuns.StatusParcela;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParcelaDto {
    private Integer numero;
    private BigDecimal valor;
    private LocalDate dataVencimento;
    private StatusParcela status; // ABERTA | PAGA | VENCIDA

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public StatusParcela getStatus() {
        return status;
    }

    public void setStatus(StatusParcela status) {
        this.status = status;
    }
}
