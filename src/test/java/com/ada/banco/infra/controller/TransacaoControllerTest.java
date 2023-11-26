package com.ada.banco.infra.controller;

import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import com.ada.banco.domain.model.enums.TipoTransacaoEnum;
import com.ada.banco.infra.gateway.bd.TransacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(scripts="../../scripts/insert_contas.sql",
                executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts="../../scripts/truncate_tables_cleanup.sql",
                executionPhase=Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class TransacaoControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Test
    public void depositar_ComSucesso_DeveRetornarStatus200() throws Exception {
        // given
        Conta turingConta = new Conta(7L, 2L, 1L);
        String requestDeposito = this.objectMapper.writeValueAsString(
                new Transacao(turingConta, BigDecimal.valueOf(8.09), TipoTransacaoEnum.DEPOSITO));

        // when
        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank-api/v1/transacao/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestDeposito))
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.jsonPath("$.message",
                                is("Depósito realizado com sucesso!"))
                );

        // then
        List<Transacao> transacoes = this.transacaoRepository.findAllByConta(turingConta);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, transacoes.size()),
                () -> Assertions.assertEquals(0,
                        BigDecimal.valueOf(8.09).compareTo(transacoes.get(0).getValor())),
                () -> Assertions.assertEquals(TipoTransacaoEnum.DEPOSITO, transacoes.get(0).getTipoTransacao()),
                () -> Assertions.assertEquals(7L, transacoes.get(0).getConta().getId()),
                () -> Assertions.assertEquals("Turing", transacoes.get(0).getConta().getTitular()),
                () -> Assertions.assertEquals(0,
                        BigDecimal.TEN.compareTo(transacoes.get(0).getConta().getSaldo()))
        );
    }

    @Test
    public void depositar_ContaInexistente_DeveRetornarStatus400() throws Exception {
        // given
        Conta turingConta = new Conta(7L, 888L, 1L);
        String requestDeposito = this.objectMapper.writeValueAsString(
                new Transacao(turingConta, BigDecimal.valueOf(8.09), TipoTransacaoEnum.DEPOSITO));

        // when then
        this.mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/bank-api/v1/transacao/depositar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestDeposito))
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.status().reason("Conta inexistente para realizar o depósito.")
                );
    }
}
