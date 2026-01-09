package com.example.gerenciador_loja_backend.services;

import com.example.gerenciador_loja_backend.dtos.PedidoDto;
import com.example.gerenciador_loja_backend.models.Cliente;
import com.example.gerenciador_loja_backend.models.ItemPedido;
import com.example.gerenciador_loja_backend.models.Pedido;
import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;
import com.example.gerenciador_loja_backend.repositories.ClienteRepository;
import com.example.gerenciador_loja_backend.repositories.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    // ==========================
    // Criar pedido
    // ==========================
    public ResponseEntity<Pedido> criarPedido(PedidoDto dto) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(dto.idCliente());
        if (clienteOptional.isEmpty()) return ResponseEntity.notFound().build();

        Cliente cliente = clienteOptional.get();
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);

        int parcelasTotais = dto.parcelasTotais() != null ? dto.parcelasTotais() : 1;
        pedido.setParcelasTotais(parcelasTotais);
        pedido.setParcelasRestantes(dto.parcelasRestantes() != null ? dto.parcelasRestantes() : parcelasTotais);

        List<ItemPedido> itens = dto.itens().stream().map(i -> {
            ItemPedido item = new ItemPedido();
            item.setNomeProduto(i.nome());
            item.setQuantidade(i.quantidade());
            item.setPrecoUnitario(i.preco() != null ? i.preco() : 0.0);
            item.setTamanho(i.tamanho());
            item.setPedido(pedido);
            return item;
        }).collect(Collectors.toList());

        pedido.setItens(itens);
        calcularValorTotal(pedido);
        atualizarStatus(pedido);

        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // ==========================
    // Buscar pedidos
    // ==========================
    public List<Pedido> getAllPedidos() {
        return pedidoRepository.findAll();
    }

    public ResponseEntity<Pedido> getOnePedido(UUID id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public List<Pedido> getPedidosPorCliente(UUID idCliente) {
        return pedidoRepository.findByClienteId(idCliente);
    }


    // Atualizar pedido parcialmente
    public ResponseEntity<Pedido> atualizarPedido(UUID id, PedidoDto dto) {
        Logger logger = LoggerFactory.getLogger(getClass());
        logger.info("=== Iniciando atualização do pedido ID={} ===", id);

        Optional<Pedido> pedidoOptional = pedidoRepository.findById(id);
        if (pedidoOptional.isEmpty()) {
            logger.warn("Pedido ID={} não encontrado", id);
            return ResponseEntity.notFound().build();
        }

        Pedido pedido = pedidoOptional.get();
        logger.info("Pedido atual: {}", pedido);

        // ============================
        // Atualiza cliente apenas se enviado
        // ============================
        if (dto.idCliente() != null) {
            Optional<Cliente> clienteOptional = clienteRepository.findById(dto.idCliente());
            clienteOptional.ifPresentOrElse(
                    pedido::setCliente,
                    () -> logger.warn("Cliente ID={} não encontrado, não foi atualizado", dto.idCliente())
            );
            logger.info("Cliente atualizado para: {}", pedido.getCliente());
        }

        // ============================
        // Atualiza parcelas
        // ============================
        if (dto.parcelasTotais() != null) {
            pedido.setParcelasTotais(dto.parcelasTotais());
            logger.info("Parcelas totais atualizadas para: {}", dto.parcelasTotais());
        }

        if (dto.parcelasRestantes() != null) {
            pedido.setParcelasRestantes(dto.parcelasRestantes());
            logger.info("Parcelas restantes definidas diretamente para: {}", dto.parcelasRestantes());
        } else if (dto.parcelasPagas() != null) {
            // Calcula automaticamente parcelasRestantes se enviado parcelasPagas
            int parcelasRestantes = Math.max(pedido.getParcelasTotais() - dto.parcelasPagas(), 0);
            pedido.setParcelasRestantes(parcelasRestantes);
            logger.info("Parcelas restantes calculadas a partir de parcelas pagas ({}): {}", dto.parcelasPagas(), parcelasRestantes);
        }

        // ============================
        // Atualiza itens
        // ============================
        if (dto.itens() != null && !dto.itens().isEmpty()) {
            pedido.getItens().clear();
            List<ItemPedido> itens = dto.itens().stream().map(i -> {
                ItemPedido item = new ItemPedido();
                item.setNomeProduto(i.nome());
                item.setQuantidade(i.quantidade());
                item.setPrecoUnitario(i.preco() != null ? i.preco() : 0.0);
                item.setTamanho(i.tamanho());
                item.setPedido(pedido);
                return item;
            }).collect(Collectors.toList());
            pedido.setItens(itens);
            logger.info("Itens atualizados: {}", itens);
        }

        // ============================
// Atualiza status de pagamento baseado no front
// ============================
            if (dto.statusDePagamento() != null) {
                StatusDePagamento statusEnum;
                switch (dto.statusDePagamento().toLowerCase()) {
                    case "paid":
                        statusEnum = StatusDePagamento.PAGO;
                        pedido.setParcelasRestantes(0); // todas pagas
                        break;

                    case "pending":
                        statusEnum = StatusDePagamento.PENDENTE;
                        break;

                    case "installment":
                        statusEnum = StatusDePagamento.PARCELADO;
                        break;

                    default:
                        logger.warn("Status do pagamento enviado pelo front é inválido: {}", dto.statusDePagamento());
                        statusEnum = null;
                        break;
                }

                if (statusEnum != null) {
                    pedido.setStatusDePagamento(statusEnum);
                    logger.info("Status do pagamento atualizado para: {}", statusEnum);
                }
            }

        else {
            // Se não veio nada, atualiza automaticamente baseado em parcelasRestantes
            if (pedido.getParcelasRestantes() == 0) {
                pedido.setStatusDePagamento(StatusDePagamento.PAGO);
            } else {
                pedido.setStatusDePagamento(StatusDePagamento.PENDENTE);
            }
        }


        // ============================
        // Atualiza valor total
        // ============================
        calcularValorTotal(pedido);
        logger.info("Valor total recalculado: {}", pedido.getValorTotal());

        // ============================
        // Atualiza status de pagamento
        // ============================
        if (pedido.getParcelasRestantes() == 0) {
            pedido.setStatusDePagamento(StatusDePagamento.PAGO);
        } else {
            pedido.setStatusDePagamento(StatusDePagamento.PENDENTE);
        }
        logger.info("Status de pagamento atualizado para: {}", pedido.getStatusDePagamento());

        // ============================
        // Salva pedido atualizado
        // ============================
        Pedido atualizado = pedidoRepository.save(pedido);
        logger.info("Pedido atualizado com sucesso: {}", atualizado);
        logger.info("=== Fim da atualização do pedido ID={} ===", id);

        return ResponseEntity.ok(atualizado);
    }


    // ==========================
    // Atualizar status de pagamento
    // ==========================
    public Pedido atualizarStatusPagamento(UUID id, StatusDePagamento novoStatus) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        pedido.setStatusDePagamento(novoStatus);

        if (novoStatus == StatusDePagamento.PAGO) {
            pedido.setParcelasRestantes(0);
        }

        return pedidoRepository.save(pedido);
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
    // Métodos auxiliares
    // ==========================
    private void calcularValorTotal(Pedido pedido) {
        double total = pedido.getItens().stream()
                .mapToDouble(item -> (item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0) * item.getQuantidade())
                .sum();
        pedido.setValorTotal(total);
    }

    private void atualizarStatus(Pedido pedido) {
        if (pedido.getParcelasRestantes() == 0) pedido.setStatusDePagamento(StatusDePagamento.PAGO);
        else pedido.setStatusDePagamento(StatusDePagamento.PENDENTE);
    }



}
