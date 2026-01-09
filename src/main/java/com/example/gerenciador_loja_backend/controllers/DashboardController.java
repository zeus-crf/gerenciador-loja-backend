package com.example.gerenciador_loja_backend.controllers;

import com.example.gerenciador_loja_backend.dtos.DashboardGraficosResponse;
import com.example.gerenciador_loja_backend.dtos.DashboardResponseDto;
import com.example.gerenciador_loja_backend.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Dashboard com filtro dinâmico de período
     *
     * Exemplos:
     * 7d  -> últimos 7 dias
     * 30d -> últimos 30 dias
     * 12m -> últimos 12 meses
     *
     * Se não enviar período → default = 7d
     */
    @GetMapping
    public ResponseEntity<DashboardResponseDto> obterDashboard(
            @RequestParam(required = false, defaultValue = "7d") String periodo
    ) {
        DashboardResponseDto response = dashboardService.obterDashboard(periodo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/graficos")
    public ResponseEntity<DashboardGraficosResponse> buscarGraficos(
            @RequestParam(defaultValue = "7d") String periodo
    ) {
        return ResponseEntity.ok(dashboardService.buscarGraficos(periodo));
    }
}
