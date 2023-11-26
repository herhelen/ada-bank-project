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
public class RealizarTransferenciaTest {

    @Mock
    private ContaGateway contaGateway;

    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private RealizarTransferencia realizarTransferencia;

    private Conta contaOrigemTeste, contaDestinoTeste;

    @BeforeEach
    public void beforeEach() {
        this.contaOrigemTeste = new Conta(20L, 1L, 3L, BigDecimal.valueOf(1212.65),
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);

        this.contaDestinoTeste = new Conta(30L, 1L, 3L, BigDecimal.ZERO,
                "Lovelace", "12345678999", TipoContaEnum.CONTA_CORRENTE);
    }

    @Test
    public void deveRealizarTransferenciaComSucesso() throws Exception {
        // Given
        BigDecimal valorTransferencia = BigDecimal.valueOf(12.60);
        Transacao transferencia = new Transacao(this.contaOrigemTeste, this.contaDestinoTeste, valorTransferencia,
                TipoTransacaoEnum.TRANSFERENCIA);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId()))
                .thenReturn(this.contaOrigemTeste);
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId()))
                .thenReturn(this.contaDestinoTeste);
        when(this.transacaoGateway.salvar(any())).thenReturn(transferencia);

        Transacao novaTransacao = this.realizarTransferencia.execute(transferencia);

        // Then
        Assertions.assertAll(
                () -> Assertions.assertEquals(this.contaOrigemTeste, novaTransacao.getConta()),
                () -> Assertions.assertEquals(this.contaDestinoTeste, novaTransacao.getContaDestino()),
                () -> Assertions.assertEquals(valorTransferencia, novaTransacao.getValor()),
                () -> Assertions.assertEquals(0,
                        BigDecimal.valueOf(1200.05).compareTo(novaTransacao.getConta().getSaldo())),
                () -> Assertions.assertEquals(0,
                        valorTransferencia.compareTo(novaTransacao.getContaDestino().getSaldo()))
        );

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId());
        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId());
        verify(this.contaGateway, times(2)).salvar(any());
        verify(this.transacaoGateway, times(1)).salvar(transferencia);
    }

    @Test
    public void deveLancarExceptionCasoContaNaoExista() {
        // Given
        BigDecimal valorTransferencia = BigDecimal.valueOf(12.60);
        Transacao transferencia = new Transacao(this.contaOrigemTeste, this.contaDestinoTeste, valorTransferencia,
                TipoTransacaoEnum.TRANSFERENCIA);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId()))
                .thenReturn(null);
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId()))
                .thenReturn(null);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarTransferencia.execute(transferencia)
        );

        Assertions.assertEquals("Conta(s) inexistente(s) para realizar a transferência.",
                throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId());
        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(transferencia);
    }

    @Test
    public void deveLancarExceptionCasoValorTransferenciaMenorIgualZero() {
        // Given
        BigDecimal valorTransferencia = BigDecimal.valueOf(-159.99);
        Transacao transferencia = new Transacao(this.contaOrigemTeste, this.contaDestinoTeste, valorTransferencia,
                TipoTransacaoEnum.TRANSFERENCIA);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId()))
                .thenReturn(this.contaOrigemTeste);
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId()))
                .thenReturn(this.contaDestinoTeste);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarTransferencia.execute(transferencia)
        );

        Assertions.assertEquals("O valor da transferência deve ser maior que nulo.", throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId());
        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(transferencia);
    }

    @Test
    public void deveLancarExceptionCasoSaldoInsuficiente() {
        // Given
        BigDecimal valorTransferencia = BigDecimal.valueOf(2000.0);
        Transacao transferencia = new Transacao(this.contaOrigemTeste, this.contaDestinoTeste, valorTransferencia,
                TipoTransacaoEnum.TRANSFERENCIA);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId()))
                .thenReturn(this.contaOrigemTeste);
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId()))
                .thenReturn(this.contaDestinoTeste);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.realizarTransferencia.execute(transferencia)
        );

        Assertions.assertEquals("A conta origem não possui saldo suficiente para realizar a transferência.",
                throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId());
        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaDestinoTeste.getAgencia(), this.contaDestinoTeste.getDigito(), this.contaDestinoTeste.getId());
        verify(this.contaGateway, times(0)).salvar(any());
        verify(this.transacaoGateway, times(0)).salvar(transferencia);
    }
}
