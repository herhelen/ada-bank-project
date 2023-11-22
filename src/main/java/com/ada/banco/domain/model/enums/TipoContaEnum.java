package com.ada.banco.domain.model.enums;

public enum TipoContaEnum {
    CONTA_CORRENTE("conta corrente"),
    POUPANCA("poupança");

    public String tipo;

    TipoContaEnum(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
