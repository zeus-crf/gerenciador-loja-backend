package com.example.gerenciador_loja_backend.dtos;

import java.util.List;

public class DashboardResponseDto {

    private Long totalPedidos;
    private Long totalClientes;
    private Long totalEstoque;
    private Long receitaTotal;

    private List<VolumePedidosDto> volumePedidos;
    private List<CrescimentoClientesDto> crescimentoClientes;

    public DashboardResponseDto(
            Long totalPedidos,
            Long totalClientes,
            Long receitaTotal,
            List<VolumePedidosDto> volumePedidos,
            List<CrescimentoClientesDto> crescimentoClientes
    ) {
        this.receitaTotal = receitaTotal;
        this.totalPedidos = totalPedidos;
        this.totalClientes = totalClientes;
        this.totalEstoque = totalEstoque;
        this.volumePedidos = volumePedidos;
        this.crescimentoClientes = crescimentoClientes;
    }

    public Long getTotalPedidos() {
        return totalPedidos;
    }

    public Long getTotalClientes() {
        return totalClientes;
    }

    public Long getTotalEstoque() {
        return totalEstoque;
    }

    public List<VolumePedidosDto> getVolumePedidos() {
        return volumePedidos;
    }

    public List<CrescimentoClientesDto> getCrescimentoClientes() {
        return crescimentoClientes;
    }

    public void setTotalPedidos(Long totalPedidos) {
        this.totalPedidos = totalPedidos;
    }

    public void setTotalClientes(Long totalClientes) {
        this.totalClientes = totalClientes;
    }

    public void setTotalEstoque(Long totalEstoque) {
        this.totalEstoque = totalEstoque;
    }

    public Long getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Long receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public void setVolumePedidos(List<VolumePedidosDto> volumePedidos) {
        this.volumePedidos = volumePedidos;
    }

    public void setCrescimentoClientes(List<CrescimentoClientesDto> crescimentoClientes) {
        this.crescimentoClientes = crescimentoClientes;
    }
}
