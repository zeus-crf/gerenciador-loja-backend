package com.example.gerenciador_loja_backend.repositories;

import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;
import com.example.gerenciador_loja_backend.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    // =====================================================
    // CONSULTAS NORMAIS
    // =====================================================

    List<Pedido> findByStatusDePagamento(StatusDePagamento status);

    List<Pedido> findByClienteId(UUID clienteId);

    // =====================================================
    // DASHBOARD - CARDS
    // =====================================================

    /**
     * Receita total no período (somente pedidos pagos)
     */
    @Query("""
        SELECT COALESCE(SUM(p.valorTotal), 0)
        FROM Pedido p
        WHERE p.statusDePagamento = :status
          AND p.dataCriacao >= :dataInicial
          AND p.dataCriacao <  :dataFinal
    """)
    Double calcularReceitaPorPeriodo(
            @Param("status") StatusDePagamento status,
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );

    /**
     * Total de pedidos no período
     */
    @Query("""
        SELECT COUNT(p)
        FROM Pedido p
        WHERE p.dataCriacao >= :dataInicial
          AND p.dataCriacao <  :dataFinal
    """)
    Long contarPedidosPorPeriodo(
            @Param("dataInicial") LocalDateTime dataInicial,
            @Param("dataFinal") LocalDateTime dataFinal
    );

    // =====================================================
    // DASHBOARD - GRÁFICOS
    // =====================================================

    /**
     * Volume de pedidos por DIA
     * Retorno: [LocalDateTime (início do dia), totalPedidos]
     */
    @Query("""
        SELECT 
            FUNCTION('date_trunc', 'day', p.dataCriacao),
            COUNT(p)
        FROM Pedido p
        WHERE p.dataCriacao >= :dataInicial
        GROUP BY FUNCTION('date_trunc', 'day', p.dataCriacao)
        ORDER BY FUNCTION('date_trunc', 'day', p.dataCriacao)
    """)
    List<Object[]> volumePedidosPorDia(
            @Param("dataInicial") LocalDateTime dataInicial
    );

    /**
     * Receita por DIA (somente pagos)
     * Retorno: [LocalDateTime (início do dia), valorTotal]
     */
    @Query("""
        SELECT 
            FUNCTION('date_trunc', 'day', p.dataCriacao),
            COALESCE(SUM(p.valorTotal), 0)
        FROM Pedido p
        WHERE p.statusDePagamento = :status
          AND p.dataCriacao >= :dataInicial
        GROUP BY FUNCTION('date_trunc', 'day', p.dataCriacao)
        ORDER BY FUNCTION('date_trunc', 'day', p.dataCriacao)
    """)
    List<Object[]> receitaPorDia(
            @Param("status") StatusDePagamento status,
            @Param("dataInicial") LocalDateTime dataInicial
    );

    // =====================================================
    // EXTRA (ESTOQUE)
    // =====================================================

    /**
     * Total de itens vendidos
     */
    @Query("""
        SELECT COALESCE(SUM(i.quantidade), 0)
        FROM Pedido p
        JOIN p.itens i
    """)
    Long calcularTotalItensEmEstoque();

    @Query("""
    SELECT COALESCE(SUM(p.valorTotal), 0)
    FROM Pedido p
    WHERE p.dataCriacao BETWEEN :inicio AND :fim
""")
    Optional<Double> sumValorTotalByDataCriacaoBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}
