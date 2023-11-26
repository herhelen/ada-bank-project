package com.ada.banco.domain.model;

import com.ada.banco.domain.model.enums.TipoContaEnum;
import com.ada.banco.domain.model.enums.TipoTransacaoEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

public class TransacaoTest {

    private Conta contaOrigemTeste, contaDestinoTeste;
    private Date dataHoraTeste;

    @BeforeEach
    public void beforeEach() {
        this.contaOrigemTeste = new Conta(20L, 1L, 3L, BigDecimal.valueOf(1212.65),
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);

        this.contaDestinoTeste = new Conta(30L, 1L, 3L, BigDecimal.ZERO,
                "Lovelace", "12345678999", TipoContaEnum.CONTA_CORRENTE);

        this.dataHoraTeste = Date.from(Instant.now());
    }

    @Test
    public void equalsTrue() {
        // Given
        BigDecimal valorTransferencia = BigDecimal.valueOf(12.60);

        Transacao transferencia = new Transacao(
                12L, this.dataHoraTeste, this.contaOrigemTeste, this.contaDestinoTeste,
                valorTransferencia, TipoTransacaoEnum.TRANSFERENCIA);

        Transacao outraTransferencia = new Transacao(
                12L, this.dataHoraTeste, this.contaOrigemTeste, this.contaDestinoTeste,
                valorTransferencia, TipoTransacaoEnum.TRANSFERENCIA);

        // When Then
        Assertions.assertEquals(transferencia, outraTransferencia);
    }

    @Test
    public void equalsFalse() {
        // Given
        BigDecimal valorDeposito = BigDecimal.valueOf(12.60);

        Transacao transferencia = new Transacao(
                12L, this.dataHoraTeste, this.contaOrigemTeste, null,
                valorDeposito, TipoTransacaoEnum.DEPOSITO);

        Transacao outraTransferencia = new Transacao(
                13L, this.dataHoraTeste, this.contaOrigemTeste, null,
                valorDeposito, TipoTransacaoEnum.SAQUE);

        // When Then
        Assertions.assertNotEquals(transferencia, outraTransferencia);
    }
}
