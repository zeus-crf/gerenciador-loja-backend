package com.example.gerenciador_loja_backend.services;

import com.example.gerenciador_loja_backend.dtos.PedidoDto;
import com.example.gerenciador_loja_backend.enuns.FormaPagamento;
import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;
import com.example.gerenciador_loja_backend.enuns.StatusParcela;
import com.example.gerenciador_loja_backend.models.*;
import com.example.gerenciador_loja_backend.repositories.ClienteRepository;
import com.example.gerenciador_loja_backend.repositories.PedidoRepository;
import com.example.gerenciador_loja_backend.repositories.ParcelaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ParcelaRepository parcelaRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ParcelaRepository parcelaRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.parcelaRepository = parcelaRepository;
    }

    // ==========================
    // Criar pedido
    // ==========================
    public ResponseEntity<Pedido> criarPedido(PedidoDto dto) {

        Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFormaPagamento(dto.formaPagamento());

        int parcelasTotais = dto.parcelasTotais() != null ? dto.parcelasTotais() : 1;
        pedido.setParcelasTotais(parcelasTotais);
        pedido.setParcelasRestantes(parcelasTotais);

        // Itens
        List<ItemPedido> itens = dto.itens().stream().map(i -> {
            ItemPedido item = new ItemPedido();
            item.setNomeProduto(i.nome());
            item.setQuantidade(i.quantidade());
            item.setPrecoUnitario(i.preco() != null ? BigDecimal.valueOf(i.preco()) : BigDecimal.ZERO);
            item.setPedido(pedido);
            return item;
        }).collect(Collectors.toList());

        pedido.setItens(itens);

        // Calcula total
        calcularValorTotal(pedido);

        // Gera parcelas
        List<Parcela> parcelas = gerarParcelas(pedido, parcelasTotais, dto.dataPrimeiroVencimento());
        parcelaRepository.saveAll(parcelas);
        pedido.setParcelas(parcelas);

        // Atualiza status pagamento
        atualizarStatusPagamento(pedido);

        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // ==========================
    // Atualizar pedido
    // ==========================
    public ResponseEntity<Pedido> atualizarPedido(UUID id, PedidoDto dto) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (dto.formaPagamento() != null) {
            pedido.setFormaPagamento(dto.formaPagamento());
        }

        // Atualiza parcelas
        if (dto.parcelasTotais() != null &&
                !dto.parcelasTotais().equals(pedido.getParcelasTotais())) {

            pedido.getParcelas().clear();
            int parcelasTotais = dto.parcelasTotais();
            pedido.setParcelasTotais(parcelasTotais);
            pedido.setParcelasRestantes(parcelasTotais);

            List<Parcela> parcelas = gerarParcelas(pedido, parcelasTotais, dto.dataPrimeiroVencimento());
            parcelaRepository.saveAll(parcelas);
            pedido.setParcelas(parcelas);
        }

        // Atualiza itens
        if (dto.itens() != null) {
            if (pedido.getItens() == null) pedido.setItens(new ArrayList<>());
            else pedido.getItens().clear();

            pedido.getItens().addAll(
                    dto.itens().stream().map(i -> {
                        ItemPedido item = new ItemPedido();
                        item.setNomeProduto(i.nome());
                        item.setQuantidade(i.quantidade());
                        item.setPrecoUnitario(i.preco() != null ? BigDecimal.valueOf(i.preco()) : BigDecimal.ZERO);
                        item.setPedido(pedido);
                        return item;
                    }).toList()
            );
        }

        // Recalcula valor total
        calcularValorTotal(pedido);
        atualizarStatusPagamento(pedido);

        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    // ==========================
    // Buscar todos pedidos
    // ==========================
    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    // ==========================
    // Buscar pedido por ID
    // ==========================
    public ResponseEntity<Pedido> getOnePedido(UUID id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==========================
    // Buscar pedidos de um cliente
    // ==========================
    public List<Pedido> getPedidosPorCliente(UUID idCliente) {
        return pedidoRepository.findByClienteId(idCliente);
    }

    // ==========================
    // Deletar pedido
    // ==========================
    public boolean deletarPedido(UUID id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ==========================
    // Atualizar status automático de pagamento
    // ==========================
    public void atualizarStatusPagamento(Pedido pedido) {
        long pagas = pedido.getParcelas().stream()
                .filter(p -> p.getStatus() == StatusParcela.PAGA)
                .count();

        pedido.setParcelasRestantes(pedido.getParcelasTotais() - (int) pagas);

        if (pedido.getParcelasRestantes() == 0) {
            pedido.setStatusDePagamento(StatusDePagamento.PAGO);
        } else if (pagas > 0) {
            pedido.setStatusDePagamento(StatusDePagamento.PARCELADO);
        } else {
            pedido.setStatusDePagamento(StatusDePagamento.PENDENTE);
        }
    }

    // ==========================
    // Atualizar parcelas vencidas
    // ==========================
    public void atualizarParcelasVencidas() {
        LocalDate hoje = LocalDate.now();
        List<Parcela> parcelas = parcelaRepository.findAll();

        parcelas.forEach(parcela -> {
            if (parcela.getStatus() == StatusParcela.ABERTA && parcela.getDataVencimento().isBefore(hoje)) {
                parcela.setStatus(StatusParcela.VENCIDA);
            }
        });

        parcelaRepository.saveAll(parcelas);
    }

    // ==========================
    // Marcar parcela como paga
    // ==========================
    public Parcela pagarParcela(UUID parcelaId) {
        Parcela parcela = parcelaRepository.findById(parcelaId)
                .orElseThrow(() -> new RuntimeException("Parcela não encontrada"));

        parcela.setStatus(StatusParcela.PAGA);
        Parcela salva = parcelaRepository.save(parcela);

        // Atualiza o pedido relacionado
        Pedido pedido = parcela.getPedido();
        atualizarStatusPagamento(pedido);
        pedidoRepository.save(pedido);

        return salva;
    }

    // ==========================
    // Calcular valor total do pedido
    // ==========================
    private void calcularValorTotal(Pedido pedido) {
        BigDecimal total = pedido.getItens().stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setValorTotal(total);
    }

    // ==========================
    // Gerar parcelas
    // ==========================
    private List<Parcela> gerarParcelas(Pedido pedido, int totalParcelas, LocalDate primeiroVencimento) {

        BigDecimal valorParcela = pedido.getValorTotal()
                .divide(BigDecimal.valueOf(totalParcelas), 2, RoundingMode.HALF_UP);

        List<Parcela> parcelas = new ArrayList<>();

        for (int i = 1; i <= totalParcelas; i++) {
            Parcela parcela = new Parcela();
            parcela.setNumero(i);
            parcela.setValor(valorParcela);
            parcela.setDataVencimento(primeiroVencimento.plusMonths(i - 1));
            parcela.setStatus(StatusParcela.ABERTA);
            parcela.setPedido(pedido);
            parcelas.add(parcela);
        }

        return parcelas;
    }
}
