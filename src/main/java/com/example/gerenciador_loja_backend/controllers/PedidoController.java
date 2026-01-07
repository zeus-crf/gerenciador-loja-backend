package com.example.gerenciador_loja_backend.controllers;

import com.example.gerenciador_loja_backend.dtos.PedidoDto;
import com.example.gerenciador_loja_backend.models.Pedido;
import com.example.gerenciador_loja_backend.services.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody PedidoDto dto) {
        return pedidoService.criarPedido(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable UUID id, @RequestBody PedidoDto dto) {
        return pedidoService.atualizarPedido(id, dto);
    }

    @GetMapping
    public List<Pedido> listarPedidos() {
        return pedidoService.getAllPedidos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPedido(@PathVariable UUID id) {
        return pedidoService.getOnePedido(id);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<Pedido> getPedidosPorCliente(@PathVariable UUID idCliente) {
        return pedidoService.getPedidosPorCliente(idCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable UUID id) {
        boolean deletado = pedidoService.deletarPedido(id);
        if (deletado) {
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

}
