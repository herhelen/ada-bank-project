package com.ada.banco.domain.usecase;

import com.ada.banco.domain.gateway.ContaGateway;
import com.ada.banco.domain.gateway.TransacaoGateway;
import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import com.ada.banco.domain.model.enums.TipoContaEnum;
import com.ada.banco.domain.model.enums.TipoTransacaoEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    private Conta contaTeste;

    @BeforeEach
    public void beforeEach() {
        this.contaTeste = new Conta(20L, 1L, 3L, BigDecimal.ONE,
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);
    }

    @Test
    public void deveRealizarDepositoComSucesso() throws Exception {
        // Given
        BigDecimal valorDeposito = BigDecimal.valueOf(350.0);
        Transacao deposito = new Transacao(this.contaTeste, valorDeposito, TipoTransacaoEnum.DEPOSITO);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(this.contaTeste);
        when(this.transacaoGateway.salvar(any())).thenReturn(deposito);

        Transacao novaTransacao = this.realizarDeposito.execute(deposito);

        // Then
        Assertions.assertAll(
                () -> Assertions.assertEquals(this.contaTeste, novaTransacao.getConta()),
                () -> Assertions.assertEquals(valorDeposito, novaTransacao.getValor()),
                () -> Assertions.assertEquals(0,
                        novaTransacao.getConta().getSaldo().compareTo(BigDecimal.valueOf(351.0)))
        );

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(1)).salvar(any());
        verify(this.transacaoGateway, times(1)).salvar(deposito);
    }

    @Test
    public void deveLancarExceptionCasoContaNaoExista() {
        // Given
        BigDecimal valorDeposito = BigDecimal.valueOf(350.0);
        Transacao deposito = new Transacao(this.contaTeste, valorDeposito, TipoTransacaoEnum.DEPOSITO);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(null);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarDeposito.execute(deposito)
        );

        Assertions.assertEquals("Conta inexistente para realizar o depósito.", throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(deposito);
    }

    @Test
    public void deveLancarExceptionCasoValorDepositoMenorIgualZero() {
        // Given
        BigDecimal valorDeposito = BigDecimal.valueOf(-159.99);
        Transacao deposito = new Transacao(this.contaTeste, valorDeposito, TipoTransacaoEnum.DEPOSITO);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(this.contaTeste);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarDeposito.execute(deposito)
        );

        Assertions.assertEquals("O valor do depósito deve ser maior que nulo.", throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(deposito);
    }
}
