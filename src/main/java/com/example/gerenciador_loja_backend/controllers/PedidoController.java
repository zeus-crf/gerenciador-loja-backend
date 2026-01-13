package com.example.gerenciador_loja_backend.controllers;

import com.example.gerenciador_loja_backend.dtos.PedidoDto;
import com.example.gerenciador_loja_backend.models.Parcela;
import com.example.gerenciador_loja_backend.models.Pedido;
import com.example.gerenciador_loja_backend.services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // ==========================
    // Listar todos os pedidos
    // ==========================
    @GetMapping
    public List<Pedido> getAll() {
        return pedidoService.getAllPedidos();
    }

    // ==========================
    // Buscar pedido por ID
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getOne(@PathVariable UUID id) {
        return pedidoService.getOnePedido(id);
    }

    // ==========================
    // Criar novo pedido
    // ==========================
    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody PedidoDto dto) {
        return pedidoService.criarPedido(dto);
    }

    // ==========================
    // Atualizar pedido
    // ==========================
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(
            @PathVariable UUID id,
            @RequestBody PedidoDto dto
    ) {
        return pedidoService.atualizarPedido(id, dto);
    }

    // ==========================
    // Pedidos de um cliente
    // ==========================
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Pedido>> getPedidosPorCliente(
            @PathVariable UUID idCliente
    ) {
        List<Pedido> pedidos = pedidoService.getPedidosPorCliente(idCliente);
        return pedidos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(pedidos);
    }

    // ==========================
    // Deletar pedido
    // ==========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        boolean deletado = pedidoService.deletarPedido(id);
        return deletado
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ==========================
    // Pedidos com parcelas vencidas
    // ==========================
    @GetMapping("/vencidos")
    public ResponseEntity<List<Pedido>> getPedidosVencidos() {
        List<Pedido> pedidosVencidos = pedidoService.getPedidosComParcelasVencidas();
        return pedidosVencidos.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(pedidosVencidos);
    }

    @PutMapping("/{id}/pagar")
    public ResponseEntity<Parcela> pagar(@PathVariable UUID id) {
        Parcela parcela = pedidoService.pagarParcela(id);
        return ResponseEntity.ok(parcela);
    }
}
