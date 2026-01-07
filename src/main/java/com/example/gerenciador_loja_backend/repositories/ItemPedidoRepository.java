package com.example.gerenciador_loja_backend.repositories;

import com.example.gerenciador_loja_backend.models.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, UUID> {
}
