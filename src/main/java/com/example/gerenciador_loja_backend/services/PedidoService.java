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

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;

    public PedidoService(PedidoRepository pedidoRepository, ClienteRepository clienteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
    }

    // Criar pedido
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

    // ============================
    // Métodos auxiliares
    // ============================
    public void calcularValorTotal(Pedido pedido) {
        double total = pedido.getItens().stream()
                .mapToDouble(item -> (item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0.0) * item.getQuantidade())
                .sum();
        pedido.setValorTotal(total);
    }

    private void atualizarStatus(Pedido pedido) {
        if (pedido.getParcelasRestantes() == 0) pedido.setStatusDePagamento(StatusDePagamento.PAGO);
        else pedido.setStatusDePagamento(StatusDePagamento.PENDENTE);
    }
    // Atualizar pedido parcialmente
    public ResponseEntity<Pedido> atualizarPedido(UUID id, PedidoDto dto) {
        Optional<Pedido> pedidoOptional = pedidoRepository.findById(id);
        if (pedidoOptional.isEmpty()) return ResponseEntity.notFound().build();

        Pedido pedido = pedidoOptional.get();

        // ============================
        // Atualiza cliente apenas se enviado
        // ============================
        if (dto.idCliente() != null) {
            Optional<Cliente> clienteOptional = clienteRepository.findById(dto.idCliente());
            clienteOptional.ifPresent(pedido::setCliente);
        }

        // ============================
        // Atualiza parcelas apenas se enviados
        // ============================
        if (dto.parcelasTotais() != null) pedido.setParcelasTotais(dto.parcelasTotais());
        if (dto.parcelasRestantes() != null) pedido.setParcelasRestantes(dto.parcelasRestantes());

        // ============================
        // Atualiza itens apenas se enviado
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
        }

        // ============================
        // Atualiza valor total e status
        // ============================
        calcularValorTotal(pedido);
        atualizarStatus(pedido);

        Pedido atualizado = pedidoRepository.save(pedido);
        return ResponseEntity.ok(atualizado);
    }

    public boolean deletarPedido(UUID id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        if (pedidoOpt.isPresent()) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
