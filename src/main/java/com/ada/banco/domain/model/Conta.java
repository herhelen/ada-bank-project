package com.ada.banco.domain.model;

import com.ada.banco.domain.model.enums.TipoContaEnum;

import java.math.BigDecimal;
import java.util.Objects;

public class Conta {
    private Long id;
    private Long agencia;
    private Long digito;
    private BigDecimal saldo;

    // Usuario / Titular
    private String titular;
    private String cpf;

    private TipoContaEnum tipoConta;

    public Conta() {
    }

    public Conta(Long id, Long agencia, Long digito, BigDecimal saldo, String titular, String cpf,
                 TipoContaEnum tipoConta) {
        this.id = id;
        this.agencia = agencia;
        this.digito = digito;
        this.saldo = saldo;
        this.titular = titular;
        this.cpf = cpf;
        this.tipoConta = tipoConta;
    }

    public Conta(Long agencia, Long digito, BigDecimal saldo, String titular, String cpf, TipoContaEnum tipoConta) {
        this.agencia = agencia;
        this.digito = digito;
        this.saldo = saldo;
        this.titular = titular;
        this.cpf = cpf;
        this.tipoConta = tipoConta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAgencia() {
        return agencia;
    }

    public void setAgencia(Long agencia) {
        this.agencia = agencia;
    }

    public Long getDigito() {
        return digito;
    }

    public void setDigito(Long digito) {
        this.digito = digito;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public TipoContaEnum getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoContaEnum tipoConta) {
        this.tipoConta = tipoConta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conta conta = (Conta) o;
        return Objects.equals(id, conta.id)
                && Objects.equals(agencia, conta.agencia)
                && Objects.equals(digito, conta.digito)
                && Objects.equals(cpf, conta.cpf)
                && tipoConta == conta.tipoConta;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, agencia, digito, cpf, tipoConta);
    }
}
