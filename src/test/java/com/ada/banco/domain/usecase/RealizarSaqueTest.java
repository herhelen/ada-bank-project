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
        Conta contaAtualizada = new Conta(20L, 1L, 3L, BigDecimal.ZERO,
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);
        Transacao saque = new Transacao(this.contaTeste, valorSaque, TipoTransacaoEnum.SAQUE);

        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId()))
                .thenReturn(this.contaTeste);
        when(this.contaGateway.salvar(any())).thenReturn(contaAtualizada);
        when(this.transacaoGateway.salvar(any())).thenReturn(saque);

        Transacao novaTransacao = this.realizarSaque.execute(saque);

        // Then
        Assertions.assertAll(
                () -> Assertions.assertEquals(this.contaTeste, novaTransacao.getConta()),
                () -> Assertions.assertEquals(valorSaque, novaTransacao.getValor()),
                () -> Assertions.assertEquals(0,
                        novaTransacao.getConta().getSaldo().compareTo(BigDecimal.ZERO))
        );

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaTeste.getAgencia(), this.contaTeste.getDigito(), this.contaTeste.getId());
        verify(this.contaGateway, times(1)).salvar(any());
        verify(this.transacaoGateway, times(1)).salvar(saque);
    }

}
