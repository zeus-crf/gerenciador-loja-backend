package com.example.gerenciador_loja_backend.repositories;

import com.example.gerenciador_loja_backend.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    // =====================================================
    // FILTRO AVANÇADO (JÁ EXISTENTE)
    // =====================================================
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
    );

    // =====================================================
    // DASHBOARD
    // =====================================================

    /**
     * Total de clientes cadastrados no período
     */
    @Query("""
        SELECT COUNT(c)
        FROM Cliente c
        WHERE c.createdAt >= :dataInicial
          AND c.createdAt <  :dataFinal
    """)
    Long contarClientesPorPeriodo(
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );

    /**
     * Crescimento de clientes agrupado por mês
     * Retorna: [mes, total]
     * Ex: 1 = Janeiro
     */
    @Query("""
        SELECT MONTH(c.createdAt), COUNT(c)
        FROM Cliente c
        WHERE c.createdAt >= :dataInicial
        GROUP BY MONTH(c.createdAt)
        ORDER BY MONTH(c.createdAt)
    """)
    List<Object[]> crescimentoClientesPorMes(
            @Param("dataInicial") LocalDateTime dataInicial
    );

    /**
     * Crescimento de clientes agrupado por dia
     * Usado para dashboards semanais
     */
    @Query("""
        SELECT DAY(c.createdAt), COUNT(c)
        FROM Cliente c
        WHERE c.createdAt >= :dataInicial
        GROUP BY DAY(c.createdAt)
        ORDER BY DAY(c.createdAt)
    """)
    List<Object[]> crescimentoClientesPorDia(
            @Param("dataInicial") LocalDateTime dataInicial
    );

    long countByCreatedAtBetween(LocalDateTime inicio, LocalDateTime fim);

}
