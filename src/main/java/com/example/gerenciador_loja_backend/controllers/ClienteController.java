package com.example.gerenciador_loja_backend.controllers;

import com.example.gerenciador_loja_backend.dtos.ClienteDto;
import com.example.gerenciador_loja_backend.dtos.ClienteFilterDto;
import com.example.gerenciador_loja_backend.dtos.ClienteFiltroRequest;
import com.example.gerenciador_loja_backend.models.Cliente;
import com.example.gerenciador_loja_backend.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(
        origins = {"http://localhost:5173", "https://gerenciador-loja-frontend-fcg8.vercel.app"},
        allowCredentials = "true"
)
public class ClienteController {

    private static final Logger log =
            LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<Cliente> criarCliente(@RequestBody @Valid ClienteDto clienteDto) {
        Cliente cliente = clienteService.criarCliente(clienteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> buscarCliente(@PathVariable UUID id) {
        Optional<ClienteDto> clienteOpt = clienteService.buscarClienteDtoPorId(id);
        return clienteOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable UUID id, @RequestBody ClienteDto clienteDto) {
        return clienteService.atualizarCliente(id, clienteDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable UUID id) {
        boolean deletado = clienteService.deletarCliente(id);
        return deletado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<Cliente>> filtrarClientes(@RequestBody ClienteFilterDto filtros) {
        List<Cliente> resultado = clienteService.filtrarClientes(filtros);
        return ResponseEntity.ok(resultado);
    }
}
