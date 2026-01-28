package com.example.gerenciador_loja_backend.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.gerenciador_loja_backend.dtos.ItemPedidoDto;
import com.example.gerenciador_loja_backend.enuns.FormaPagamento;
import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;

public record PedidoDto(
        String idCliente,

        Integer parcelasTotais,
        Integer parcelasPagas,
        Integer parcelasRestantes,

        StatusDePagamento statusDePagamento,
        FormaPagamento formaPagamento,

        LocalDate dataPrimeiroVencimento,
        Double valorParcelas,
        Integer diaVencimento,

        List<ItemPedidoDto> itens,
        List<ParcelaDto> parcelas
) {}

