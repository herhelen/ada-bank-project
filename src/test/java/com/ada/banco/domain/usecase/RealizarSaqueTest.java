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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RealizarSaqueTest {

    @Mock
    private ContaGateway contaGateway;

    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private RealizarSaque realizarSaque;

    private Conta contaTeste;

    @BeforeEach
    public void beforeEach() {
        this.contaTeste = new Conta(20L, 1L, 3L, BigDecimal.valueOf(350.0),
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);
    }

    @Test
    public void deveRealizarSaqueComSucesso() throws Exception {
        // Given
        BigDecimal valorSaque = BigDecimal.valueOf(350.0);
        Transacao saque = new Transacao(this.contaTeste, valorSaque);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(this.contaTeste);
        when(this.transacaoGateway.salvar(any())).thenReturn(saque);

        Transacao novaTransacao = this.realizarSaque.execute(saque);

        // Then
        Assertions.assertAll(
                () -> Assertions.assertEquals(TipoTransacaoEnum.SAQUE, novaTransacao.getTipoTransacao()),
                () -> Assertions.assertEquals(this.contaTeste, novaTransacao.getConta()),
                () -> Assertions.assertEquals(valorSaque, novaTransacao.getValor()),
                () -> Assertions.assertEquals(0,
                        BigDecimal.ZERO.compareTo(novaTransacao.getConta().getSaldo()))
        );

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(1)).salvar(any());
        verify(this.transacaoGateway, times(1)).salvar(saque);
    }

    @Test
    public void deveLancarExceptionCasoContaNaoExista() {
        // Given
        BigDecimal valorSaque = BigDecimal.valueOf(350.0);
        Transacao saque = new Transacao(this.contaTeste, valorSaque);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(null);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarSaque.execute(saque)
        );

        Assertions.assertEquals("Conta inexistente para realizar o saque.", throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(saque);
    }

    @Test
    public void deveLancarExceptionCasoValorSaqueMenorIgualZero() {
        // Given
        BigDecimal valorSaque = BigDecimal.valueOf(-159.99);
        Transacao saque = new Transacao(this.contaTeste, valorSaque);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(this.contaTeste);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarSaque.execute(saque)
        );

        Assertions.assertEquals("O valor do saque deve ser maior que nulo.", throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(saque);
    }

    @Test
    public void deveLancarExceptionCasoSaldoInsuficiente() {
        // Given
        BigDecimal valorSaque = BigDecimal.valueOf(350.1);
        Transacao saque = new Transacao(this.contaTeste, valorSaque);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(this.contaTeste);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarSaque.execute(saque)
        );

        Assertions.assertEquals("A conta não possui saldo suficiente para realizar o saque.",
                throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(saque);
    }
}
