package com.example.gerenciador_loja_backend.models;

import com.example.gerenciador_loja_backend.enuns.StatusDePagamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue
    private UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ItemPedido> itens = new ArrayList<>();

    private Double valorTotal;

    private Integer parcelasTotais;

    private Integer parcelasRestantes;

    private double valorParcelas;

    @Enumerated(EnumType.STRING)
    private StatusDePagamento statusDePagamento;

    // ===============================
    // GETTERS E SETTERS
    // ===============================
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) {
        this.itens.clear();
        if (itens != null) {
            itens.forEach(this::addItem);
        }
    }

    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

    public Integer getParcelasTotais() { return parcelasTotais; }
    public void setParcelasTotais(Integer parcelasTotais) { this.parcelasTotais = parcelasTotais; }

    public Integer getParcelasRestantes() { return parcelasRestantes; }
    public void setParcelasRestantes(Integer parcelasRestantes) { this.parcelasRestantes = parcelasRestantes; }

    public StatusDePagamento getStatusDePagamento() { return statusDePagamento; }
    public void setStatusDePagamento(StatusDePagamento statusDePagamento) { this.statusDePagamento = statusDePagamento; }

    public double getValorParcelas() {
        return valorParcelas;
    }

    public void setValorParcelas(double valorParcelas) {
        this.valorParcelas = valorParcelas;
    }

    // ===============================
    // MÉTODOS DE CONVENIÊNCIA
    // ===============================
    public void addItem(ItemPedido item) {
        itens.add(item);
        item.setPedido(this);
    }

    public void removeItem(ItemPedido item) {
        itens.remove(item);
        item.setPedido(null);
    }
}
