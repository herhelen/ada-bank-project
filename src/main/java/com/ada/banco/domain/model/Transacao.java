package com.ada.banco.domain.model;

import com.ada.banco.domain.model.enums.TipoTransacaoEnum;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date dataHora;

    @ManyToOne
    private Conta conta;

    @ManyToOne
    private Conta contaDestino;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private TipoTransacaoEnum tipoTransacao;

    public Transacao() {
    }

    public Transacao(Conta conta, BigDecimal valor) {
        this.conta = conta;
        this.valor = valor;
    }

    public Transacao(Conta conta, Conta contaDestino, BigDecimal valor) {
        this.conta = conta;
        this.contaDestino = contaDestino;
        this.valor = valor;
    }

    public Transacao(Long id, Date dataHora, Conta conta, Conta contaDestino, BigDecimal valor,
                     TipoTransacaoEnum tipoTransacao) {
        this.id = id;
        this.dataHora = dataHora;
        this.conta = conta;
        this.contaDestino = contaDestino;
        this.valor = valor;
        this.tipoTransacao = tipoTransacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataHora() {
        return dataHora;
    }

    public void setDataHora(Date dataHora) {
        this.dataHora = dataHora;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Conta getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(Conta contaDestino) {
        this.contaDestino = contaDestino;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoTransacaoEnum getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacaoEnum tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transacao transacao = (Transacao) o;
        return Objects.equals(dataHora, transacao.dataHora)
                && Objects.equals(conta, transacao.conta)
                && Objects.equals(contaDestino, transacao.contaDestino)
                && Objects.equals(valor, transacao.valor)
                && tipoTransacao == transacao.tipoTransacao;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataHora, conta, contaDestino, valor, tipoTransacao);
    }
}
