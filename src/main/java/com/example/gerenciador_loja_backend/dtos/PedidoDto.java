package com.example.gerenciador_loja_backend.dtos;

import java.util.List;
import java.util.UUID;

import com.example.gerenciador_loja_backend.dtos.ItemPedidoDto;
import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;

public record PedidoDto(
        UUID idCliente,
        List<ItemPedidoDto> itens,          // <--- aqui usamos o DTO
        String statusDePagamento,
        Integer parcelasTotais,
        Integer parcelasRestantes,
        Integer parcelasPagas
) {}
