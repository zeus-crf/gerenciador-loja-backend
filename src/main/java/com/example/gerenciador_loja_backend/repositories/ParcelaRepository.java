package com.example.gerenciador_loja_backend.repositories;

import com.example.gerenciador_loja_backend.models.Parcela;
import com.example.gerenciador_loja_backend.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParcelaRepository extends JpaRepository<Parcela, UUID > {
    public List<Parcela> listAll(Pedido pedido);
}
