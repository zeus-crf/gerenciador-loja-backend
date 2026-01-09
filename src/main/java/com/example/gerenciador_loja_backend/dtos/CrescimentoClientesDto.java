package com.example.gerenciador_loja_backend.dtos;

public class CrescimentoClientesDto {

    /**
     * Dia do período
     * Ex:
     *  - 1, 2, 3... (para 7d / 30d)
     *  - mês (1–12) se for 12m
     */
    private Integer dia;

    /**
     * Total de clientes cadastrados nesse dia
     */
    private Long totalClientes;

    public CrescimentoClientesDto() {}

    public CrescimentoClientesDto(Integer dia, Long totalClientes) {
        this.dia = dia;
        this.totalClientes = totalClientes;
    }

    public Integer getDia() {
        return dia;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public Long getTotalClientes() {
        return totalClientes;
    }

    public void setTotalClientes(Long totalClientes) {
        this.totalClientes = totalClientes;
    }
}
