package com.example.gerenciador_loja_backend.dtos;

import java.util.UUID;

public record ItemPedidoDto(
        UUID id,
        String nome,
        Double preco,
        String tamanho,
        Integer quantidade  // novo campo
) {

    // Calcula o valor total deste item
    public double calcularTotalItem() {
        if (preco == null || quantidade == null) {
            return 0.0;
        }
        return preco * quantidade;
    }
}
