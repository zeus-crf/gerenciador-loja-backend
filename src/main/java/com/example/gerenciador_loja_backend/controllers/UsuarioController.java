package com.example.gerenciador_loja_backend.controllers;

import com.example.gerenciador_loja_backend.dtos.UsuarioCreateDto;
import com.example.gerenciador_loja_backend.dtos.UsuarioResponseDto;
import com.example.gerenciador_loja_backend.dtos.UsuarioUpdateDto;
import com.example.gerenciador_loja_backend.services.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponseDto> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> criar(
            @RequestBody UsuarioCreateDto dto
    ) {
        System.out.println("📥 [USUÁRIOS] Criando usuário: " + dto.getUsername());

        UsuarioResponseDto usuarioCriado = service.criarUsuario(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioCriado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> atualizar(
            @PathVariable UUID id,
            @RequestBody UsuarioUpdateDto dto
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }
}