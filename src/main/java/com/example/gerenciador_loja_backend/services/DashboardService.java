package com.example.gerenciador_loja_backend.services;

import com.example.gerenciador_loja_backend.dtos.CrescimentoClientesDto;
import com.example.gerenciador_loja_backend.dtos.DashboardGraficosResponse;
import com.example.gerenciador_loja_backend.dtos.DashboardResponseDto;
import com.example.gerenciador_loja_backend.dtos.VolumePedidosDto;
import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;
import com.example.gerenciador_loja_backend.repositories.ClienteRepository;
import com.example.gerenciador_loja_backend.repositories.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public DashboardService(
            ClienteRepository clienteRepository,
            PedidoRepository pedidoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public DashboardResponseDto obterDashboard(String periodo) {

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataInicial = calcularDataInicial(periodo, agora);

        // Total de clientes e pedidos
        Long totalClientes = clienteRepository.contarClientesPorPeriodo(dataInicial, agora);
        Long totalPedidos = pedidoRepository.contarPedidosPorPeriodo(dataInicial, agora);

        // Receita total (somente pedidos pagos)
        Double receita = pedidoRepository.calcularReceitaPorPeriodo(
                StatusDePagamento.PAGO,
                dataInicial,
                agora
        );
        Long receitaTotal = receita != null ? receita.longValue() : 0L;

        // Séries para gráficos
        List<VolumePedidosDto> volumePedidos = montarSeriePedidos(periodo, dataInicial);
        List<CrescimentoClientesDto> crescimentoClientes = montarSerieClientes(periodo, dataInicial);

        return new DashboardResponseDto(
                totalPedidos,
                totalClientes,
                receitaTotal,
                volumePedidos,
                crescimentoClientes
        );
    }

    // =====================================================
    // AUXILIARES
    // =====================================================

    private LocalDateTime calcularDataInicial(String periodo, LocalDateTime agora) {
        if (periodo == null || periodo.isBlank()) {
            return agora.minusDays(7);
        }
        if (periodo.endsWith("d")) {
            return agora.minusDays(Integer.parseInt(periodo.replace("d", "")));
        }
        if (periodo.endsWith("m")) {
            return agora.minusMonths(Integer.parseInt(periodo.replace("m", "")));
        }
        return agora.minusDays(7);
    }

    private List<VolumePedidosDto> montarSeriePedidos(String periodo, LocalDateTime dataInicial) {
        List<Object[]> dados = pedidoRepository.volumePedidosPorDia(dataInicial);
        List<VolumePedidosDto> serie = new ArrayList<>();

        for (Object[] row : dados) {
            // row[0] = LocalDateTime, row[1] = total de pedidos
            LocalDateTime dia = (LocalDateTime) row[0];
            Long total = ((Number) row[1]).longValue();

            int valor = "12m".equals(periodo) ? dia.getMonthValue() : dia.getDayOfMonth();
            serie.add(new VolumePedidosDto(valor, total));
        }

        return serie;
    }

    private List<CrescimentoClientesDto> montarSerieClientes(String periodo, LocalDateTime dataInicial) {
        List<Object[]> dados =
                "12m".equals(periodo)
                        ? clienteRepository.crescimentoClientesPorMes(dataInicial)
                        : clienteRepository.crescimentoClientesPorDia(dataInicial);

        List<CrescimentoClientesDto> serie = new ArrayList<>();

        for (Object[] row : dados) {
            // row[0] já é Integer (dia ou mês), row[1] é Long
            Integer diaOuMes = ((Number) row[0]).intValue();
            Long total = ((Number) row[1]).longValue();
            serie.add(new CrescimentoClientesDto(diaOuMes, total));
        }

        return serie;
    }

    public DashboardGraficosResponse buscarGraficos(String periodo) {

        List<String> labels = new ArrayList<>();
        List<Long> clientes = new ArrayList<>();
        List<Double> vendas = new ArrayList<>();

        LocalDate hoje = LocalDate.now();

        if ("12m".equals(periodo)) {

            LocalDate inicio = hoje.minusMonths(11).withDayOfMonth(1);

            for (int i = 0; i < 12; i++) {
                LocalDate mes = inicio.plusMonths(i);

                labels.add(
                        mes.getMonth()
                                .getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"))
                );

                LocalDateTime inicioMes = mes.atStartOfDay();
                LocalDateTime fimMes = mes.withDayOfMonth(mes.lengthOfMonth())
                        .atTime(LocalTime.MAX);

                long novosClientes =
                        clienteRepository.countByCreatedAtBetween(inicioMes, fimMes);

                double totalVendas =
                        pedidoRepository.sumValorTotalByDataCriacaoBetween(inicioMes, fimMes)
                                .orElse(0.0);

                clientes.add(novosClientes);
                vendas.add(totalVendas);
            }

            return new DashboardGraficosResponse(labels, clientes, vendas);
        }

        // =======================
        // 7d e 30d → diário
        // =======================
        int dias = "30d".equals(periodo) ? 30 : 7;

        LocalDate inicio = hoje.minusDays(dias);

        for (int i = 0; i <= dias; i++) {
            LocalDate dia = inicio.plusDays(i);

            labels.add(dia.format(DateTimeFormatter.ofPattern("dd/MM")));

            long novosClientes =
                    clienteRepository.countByCreatedAtBetween(
                            dia.atStartOfDay(),
                            dia.atTime(LocalTime.MAX)
                    );

            double totalVendas =
                    pedidoRepository.sumValorTotalByDataCriacaoBetween(
                            dia.atStartOfDay(),
                            dia.atTime(LocalTime.MAX)
                    ).orElse(0.0);

            clientes.add(novosClientes);
            vendas.add(totalVendas);
        }

        return new DashboardGraficosResponse(labels, clientes, vendas);
    }

}

