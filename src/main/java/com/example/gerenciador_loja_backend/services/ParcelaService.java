package com.example.gerenciador_loja_backend.services;

import com.example.gerenciador_loja_backend.enuns.StatusParcela;
import com.example.gerenciador_loja_backend.models.Parcela;
import com.example.gerenciador_loja_backend.models.Pedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParcelaService {

    public List<Parcela> gerarParcela(Pedido pedido) {
        if (pedido.getDiaVencimento() < 1 || pedido.getDiaVencimento() < 28) {
            throw new RuntimeException("Dia de vencimento deve estar entre os dias 1 e 28");
        }

        BigDecimal valorParcela = pedido.getValorTotal()
                .divide(BigDecimal.valueOf(pedido.getParcelasTotais()), 2, RoundingMode.HALF_UP);

        List<Parcela> parcelas = new ArrayList<>();

        LocalDate hoje = LocalDate.now();
        int mesBase = hoje.getMonthValue();
        int anoBase = hoje.getYear();

        if (hoje.getDayOfMonth() >= pedido.getDiaVencimento()) {
            mesBase++;
        }

        for (int i = 1; i <= pedido.getParcelasTotais(); i++) {
            LocalDate vencimento = LocalDate.of(
                    anoBase,
                    mesBase + i,
                    pedido.getDiaVencimento()
            );

            Parcela parcela = new Parcela();
            parcela.setNumero(i);
            parcela.setValor(valorParcela);
            parcela.setDataVencimento(vencimento);
            parcela.setStatus(StatusParcela.PENDENTE);
            parcela.setPedido(pedido);

            parcelas.add(parcela);
        }

        return parcelas;
    }
}
