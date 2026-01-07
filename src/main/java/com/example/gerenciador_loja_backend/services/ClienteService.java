package com.example.gerenciador_loja_backend.services;

import com.example.gerenciador_loja_backend.dtos.ClienteDto;
import com.example.gerenciador_loja_backend.dtos.ClienteFilterDto;
import com.example.gerenciador_loja_backend.dtos.ClienteFiltroRequest;
import com.example.gerenciador_loja_backend.models.Cliente;
import com.example.gerenciador_loja_backend.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;


import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente criarCliente(ClienteDto dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome() != null ? dto.nome() : "");
        cliente.setTelefone(dto.telefone() != null ? dto.telefone() : "");
        cliente.setEmail(dto.email() != null ? dto.email() : "");
        cliente.setEndereco(dto.endereco() != null ? dto.endereco() : "");
        cliente.setNotas(dto.notas() != null ? dto.notas() : "");
        cliente.setCreatedAt(LocalDateTime.now());

        return clienteRepository.save(cliente);
    }


    public List<Cliente> listarClientes() {
        return clienteRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    public Optional<ClienteDto> buscarClienteDtoPorId(UUID id) {
        return clienteRepository.findById(id)
                .map(c -> new ClienteDto(
                        c.getId(),
                        c.getNome(),       // ⚠️ enviado para "nome" no DTO
                        c.getTelefone(),
                        c.getEmail(),
                        c.getEndereco(),
                        c.getNotas(),
                        c.getCreatedAt()
                ));
    }

    public Optional<Cliente> atualizarCliente(UUID id, ClienteDto dto) {
        return clienteRepository.findById(id).map(cliente -> {
            if (dto.nome() != null) cliente.setNome(dto.nome());
            if (dto.telefone() != null) cliente.setTelefone(dto.telefone());
            if (dto.email() != null) cliente.setEmail(dto.email());
            if (dto.endereco() != null) cliente.setEndereco(dto.endereco());
            if (dto.notas() != null) cliente.setNotas(dto.notas());
            return clienteRepository.save(cliente);
        });
    }

    public boolean deletarCliente(UUID id) {
        if (!clienteRepository.existsById(id)) return false;
        clienteRepository.deleteById(id);
        return true;
    }

    public List<Cliente> filtrarClientes(ClienteFilterDto filtros) {
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes.stream()
                .filter(c -> filtros.getNome() == null || c.getNome().toLowerCase().contains(filtros.getNome().toLowerCase()))
                .filter(c -> filtros.getEmail() == null || (c.getEmail() != null && c.getEmail().toLowerCase().contains(filtros.getEmail().toLowerCase())))
                .filter(c -> filtros.getTelefone() == null || (c.getTelefone() != null && c.getTelefone().contains(filtros.getTelefone())))
                .filter(c -> filtros.getEndereco() == null || (c.getEndereco() != null && c.getEndereco().toLowerCase().contains(filtros.getEndereco().toLowerCase())))
                .filter(c -> filtros.getDataInicial() == null || (c.getCreatedAt() != null && !c.getCreatedAt().toLocalDate().isBefore(filtros.getDataInicial())))
                .filter(c -> filtros.getDataFinal() == null || (c.getCreatedAt() != null && !c.getCreatedAt().toLocalDate().isAfter(filtros.getDataFinal())))
                .sorted(getComparator(filtros.getOrdenacao()))
                .collect(Collectors.toList());
    }

    private Comparator<Cliente> getComparator(String ordenacao) {
        if ("ANTIGO".equalsIgnoreCase(ordenacao)) {
            return Comparator.comparing(Cliente::getCreatedAt);
        }
        // RECENTE ou default
        return Comparator.comparing(Cliente::getCreatedAt).reversed();
    }




}
