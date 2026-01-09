package com.example.gerenciador_loja_backend.dtos;

public class VolumePedidosDto {

    /**
     * Dia do período
     * Ex:
     *  - 1, 2, 3... (para 7d / 30d)
     *  - mês (1–12) se for 12m
     */
    private Integer dia;

    /**
     * Total de pedidos nesse dia
     */
    private Long totalPedidos;

    public VolumePedidosDto() {}

    public VolumePedidosDto(Integer dia, Long totalPedidos) {
        this.dia = dia;
        this.totalPedidos = totalPedidos;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Long getTotalPedidos() {
        return totalPedidos;
    }

    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }
}
