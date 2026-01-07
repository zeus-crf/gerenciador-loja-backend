package com.example.gerenciador_loja_backend.services;

import com.example.gerenciador_loja_backend.dtos.UsuarioCreateDto;
import com.example.gerenciador_loja_backend.dtos.UsuarioResponseDto;
import com.example.gerenciador_loja_backend.dtos.UsuarioUpdateDto;
import com.example.gerenciador_loja_backend.models.Usuario;
import com.example.gerenciador_loja_backend.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.gerenciador_loja_backend.security.SecurityUtils;


import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository repository,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // LISTAR USUÁRIOS
    // =========================
    public List<UsuarioResponseDto> listar() {
        return repository.findAll()
                .stream()
                .map(u -> new UsuarioResponseDto(
                        u.getId(),
                        u.getUsername()
                ))
                .toList();
    }

    // =========================
    // CRIAR USUÁRIO
    // =========================
    public UsuarioResponseDto criarUsuario(UsuarioCreateDto dto) {

        if (repository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        Usuario salvo = repository.save(usuario);

        return new UsuarioResponseDto(
                salvo.getId(),
                salvo.getUsername()
        );
    }

    // =========================
    // ATUALIZAR USUÁRIO
    // =========================
    public UsuarioResponseDto atualizar(UUID id, UsuarioUpdateDto dto) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            usuario.setUsername(dto.getUsername());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Usuario salvo = repository.save(usuario);

        return new UsuarioResponseDto(
                salvo.getId(),
                salvo.getUsername()
        );
    }

    // =========================
    // DELETAR USUÁRIO
    // =========================
    public void deletar(UUID id) {

        // 🔍 Verifica se existe
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuário não encontrado"
            );
        }

        // 🔐 ID do usuário logado
        UUID usuarioLogadoId = SecurityUtils.getUsuarioLogadoId();

        // 🚫 Impede excluir a si mesmo
        if (id.equals(usuarioLogadoId)) {
            throw new AccessDeniedException("Você não pode excluir o próprio usuário");
        }

        // 🗑️ Exclusão
        repository.deleteById(id);
    }

}
