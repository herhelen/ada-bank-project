package com.ada.banco.domain.model;

import com.ada.banco.domain.model.enums.TipoContaEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ContaTest {

    @Test
    public void equalsTrue() {
        // Given
        Conta conta =
                new Conta(0L, 1L, 3L, BigDecimal.ZERO, "Pedro", "222222222",
                        TipoContaEnum.CONTA_CORRENTE);

        Conta outraConta =
                new Conta(0L, 1L, 3L, BigDecimal.ZERO, "Pedro", "222222222",
                        TipoContaEnum.CONTA_CORRENTE);

        // When Then
        Assertions.assertEquals(conta, outraConta);
    }

    @Test
    public void equalsFalse() {
        // Given
        Conta conta =
                new Conta(0L, 1L, 3L, BigDecimal.ZERO, "Pedro", "222222222",
                        TipoContaEnum.CONTA_CORRENTE);

        Conta outraConta =
                new Conta(10L, 1L, 3L, BigDecimal.TEN, "Pedro", "222222222",
                        TipoContaEnum.CONTA_CORRENTE);


        // When Then
        Assertions.assertNotEquals(conta, outraConta);
    }
}
