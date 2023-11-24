package com.ada.banco.domain.model.enums;

public enum TipoTransacaoEnum {
    DEPOSITO("depósito"),
    SAQUE("saque"),
    TRANSFERENCIA("transferência");

    public String tipoTransacao;

    TipoTransacaoEnum(String tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public String getTipoTransacao() {
        return tipoTransacao;
    }
}
