package com.example.gerenciador_loja_backend.dtos;

import java.util.List;

public record DashboardGraficosResponse(
        List<String> labels,
        List<Long> clientes,
        List<Double> vendas
) {}