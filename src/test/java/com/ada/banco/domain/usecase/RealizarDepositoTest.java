package com.ada.banco.domain.usecase;

import com.ada.banco.domain.gateway.ContaGateway;
import com.ada.banco.domain.gateway.TransacaoGateway;
import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import com.ada.banco.domain.model.enums.TipoContaEnum;
import com.ada.banco.domain.model.enums.TipoTransacaoEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RealizarDepositoTest {

    @Mock
    private ContaGateway contaGateway;

    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private RealizarDeposito realizarDeposito;

    @Test
    public void deveRealizarDepositoComSucesso() throws Exception {
        // Given
        BigDecimal valorDeposito = BigDecimal.valueOf(350.0);
        Conta conta = new Conta(20L, 1L, 3L, BigDecimal.ONE,
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);
        Conta contaAtualizada = new Conta(20L, 1L, 3L, valorDeposito.add(BigDecimal.ONE),
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);
        Transacao deposito = new Transacao(conta, valorDeposito, TipoTransacaoEnum.DEPOSITO);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                conta.getAgencia(), conta.getDigito(), conta.getId())).thenReturn(conta);
        when(this.contaGateway.salvar(any())).thenReturn(contaAtualizada);
        when(this.transacaoGateway.salvar(any())).thenReturn(deposito);

        Transacao novaTransacao = this.realizarDeposito.execute(deposito);

        // Then
        Assertions.assertAll(
                () -> Assertions.assertEquals(conta, novaTransacao.getConta()),
                () -> Assertions.assertEquals(valorDeposito, novaTransacao.getValor()),
                () -> Assertions.assertEquals(0,
                        novaTransacao.getConta().getSaldo().compareTo(BigDecimal.valueOf(351.0)))
        );

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                conta.getAgencia(), conta.getDigito(), conta.getId());
        verify(this.contaGateway, times(1)).salvar(contaAtualizada);
        verify(this.transacaoGateway, times(1)).salvar(deposito);
    }

}
