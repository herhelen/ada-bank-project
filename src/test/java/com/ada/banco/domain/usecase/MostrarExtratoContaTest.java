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
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MostrarExtratoContaTest {

    @Mock
    private ContaGateway contaGateway;

    @Mock
    private TransacaoGateway transacaoGateway;

    @InjectMocks
    private MostrarExtratoConta mostrarExtratoConta;

    private Conta contaOrigemTeste, contaDestinoTeste;
    private Transacao transferenciaTeste;
    private List<Transacao> transacoesTeste;

    @BeforeEach
    public void beforeEach() {
        this.contaOrigemTeste = new Conta(20L, 1L, 3L, BigDecimal.valueOf(1200.05),
                "Ada", "12345678900", TipoContaEnum.CONTA_CORRENTE);

        this.contaDestinoTeste = new Conta(30L, 1L, 3L,  BigDecimal.valueOf(12.60),
                "Lovelace", "12345678999", TipoContaEnum.CONTA_CORRENTE);

        this.transferenciaTeste = new Transacao(1L, Date.from(Instant.now()),
                this.contaOrigemTeste, this.contaDestinoTeste,
                BigDecimal.valueOf(12.60), TipoTransacaoEnum.TRANSFERENCIA);

        this.transacoesTeste = List.of(new Transacao[] { this.transferenciaTeste });
    }

    @Test
    public void deveMostrarExtratoComSucesso() throws Exception {
        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId()))
                .thenReturn(this.contaOrigemTeste);
        when(this.transacaoGateway.buscaPorConta(this.contaOrigemTeste))
                .thenReturn(this.transacoesTeste);

        List<Transacao> transacoes = this.mostrarExtratoConta.execute(this.contaOrigemTeste);

        // Then
        Assertions.assertEquals(transacoesTeste, transacoes);

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId());
        verify(this.transacaoGateway, times(1)).buscaPorConta(any());
    }

    @Test
    public void deveLancarExceptionCasoContaNaoExista() {
        // When
        when(this.contaGateway.buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId()))
                .thenReturn(null);

        // Then
        Throwable throwable = Assertions.assertThrows(
                Exception.class,
                () -> this.mostrarExtratoConta.execute(this.contaOrigemTeste)
        );

        Assertions.assertEquals("Não é possível mostrar o extrato de uma conta inexistente.",
                throwable.getMessage());

        verify(this.contaGateway, times(1)).buscarPorAgenciaDigitoEConta(
                this.contaOrigemTeste.getAgencia(), this.contaOrigemTeste.getDigito(), this.contaOrigemTeste.getId());
        verify(this.transacaoGateway, times(0)).buscaPorConta(any());
    }
}
