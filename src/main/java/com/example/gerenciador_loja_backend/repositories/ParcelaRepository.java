package com.example.gerenciador_loja_backend.repositories;

import com.example.gerenciador_loja_backend.models.Parcela;
import com.example.gerenciador_loja_backend.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParcelaRepository extends JpaRepository<Parcela, UUID> {
    List<Parcela> findByPedido(Pedido pedido);
}
