package com.example.gerenciador_loja_backend.repositories;

import com.example.gerenciador_loja_backend.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    @Query("""
    SELECT c FROM Cliente c
    WHERE (:nome IS NULL OR LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')))
      AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))
      AND (:telefone IS NULL OR c.telefone LIKE CONCAT('%', :telefone, '%'))
      AND (:endereco IS NULL OR LOWER(c.endereco) LIKE LOWER(CONCAT('%', :endereco, '%')))
      AND c.createdAt >= COALESCE(:dataInicial, c.createdAt)
      AND c.createdAt <  COALESCE(:dataFinal, c.createdAt)
""")
    List<Cliente> filtrar(
            @Param("nome") String nome,
            @Param("email") String email,
            @Param("telefone") String telefone,
            @Param("endereco") String endereco,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );}

