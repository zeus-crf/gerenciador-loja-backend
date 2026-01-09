package com.example.gerenciador_loja_backend.controllers;

import com.example.gerenciador_loja_backend.dtos.PedidoDto;
import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;
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
    // CRUD pedidos
    // ==========================
    @GetMapping
    public List<Pedido> getAll() {
        return pedidoService.getAllPedidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getOne(@PathVariable UUID id) {
        return pedidoService.getOnePedido(id);
    }

    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody PedidoDto dto) {
        return pedidoService.criarPedido(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pedido> atualizar(@PathVariable UUID id, @RequestBody PedidoDto dto) {
        return pedidoService.atualizarPedido(id, dto);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Pedido>> getPedidosPorCliente(@PathVariable UUID idCliente) {
        List<Pedido> pedidos = pedidoService.getPedidosPorCliente(idCliente);
        if (pedidos.isEmpty()) {
            return ResponseEntity.noContent().build(); // ou ok com lista vazia
        }
        return ResponseEntity.ok(pedidos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        boolean deletado = pedidoService.deletarPedido(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable UUID id, @RequestBody PedidoDto dto) {
        return pedidoService.atualizarPedido(id, dto);
    }
}
