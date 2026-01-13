package com.example.gerenciador_loja_backend.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.gerenciador_loja_backend.dtos.ItemPedidoDto;
import com.example.gerenciador_loja_backend.enuns.FormaPagamento;
import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;

public record PedidoDto(
        UUID idCliente,
        List<ItemPedidoDto> itens,
        String statusDePagamento,
        Integer parcelasTotais,
        Integer parcelasRestantes,
        Integer parcelasPagas,
        FormaPagamento formaPagamento,
        Integer diaVencimento,
        LocalDate dataPrimeiroVencimento,
        List<LocalDate> datasVencimento
) { }
