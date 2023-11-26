package com.ada.banco.infra.controller;

import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.enums.TipoContaEnum;
import com.ada.banco.infra.gateway.bd.ContaRepository;
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

import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class ContaControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    @Sql(scripts="../../scripts/truncate_tables_cleanup.sql",
            executionPhase=Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void criarConta_ComSucesso_DeveRetornarStatus201() throws Exception {
        // given
        String requestConta = this.objectMapper.writeValueAsString(
                new Conta(1L, 3L, BigDecimal.ZERO, "Pedro", "222222222",
                        TipoContaEnum.POUPANCA));

        // when
        this.mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/bank-api/v1/contas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestConta))
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.jsonPath("$.message",
                                is("Nova conta criada com sucesso!"))
                );

        // then
        Conta contaCriada = this.contaRepository.findByCpf("222222222");
        Assertions.assertEquals("Pedro", contaCriada.getTitular());
    }

    @Test
    @Sql(scripts="../../scripts/truncate_tables_cleanup.sql",
            executionPhase=Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void criarConta_JaExistente_DeveRetornarStatus400() throws Exception {
        // given
        String requestConta = this.objectMapper.writeValueAsString(
                new Conta(1L, 3L, BigDecimal.ZERO, "Pedro", "222222222",
                        TipoContaEnum.POUPANCA));

        this.mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/bank-api/v1/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestConta));

        // when
        this.mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/bank-api/v1/contas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestConta))
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.status().reason("Erro ao criar nova conta")
                );
    }

    @Test
    @SqlGroup({
            @Sql(scripts="../../scripts/insert_contas.sql",
                    executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD),
            @Sql(scripts="../../scripts/truncate_tables_cleanup.sql",
                    executionPhase=Sql.ExecutionPhase.AFTER_TEST_METHOD)
    })
    public void listarTodasContas_DeveRetornarStatus200() throws Exception {

        // when
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/bank-api/v1/contas"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // then
        Conta contaLovelace = this.contaRepository.findByCpf("11122233344488");
        Assertions.assertAll(
                () -> Assertions.assertEquals(4L, contaLovelace.getId()),
                () -> Assertions.assertEquals(1L, contaLovelace.getAgencia()),
                () -> Assertions.assertEquals(1L, contaLovelace.getDigito()),
                () -> Assertions.assertEquals(0, contaLovelace.getSaldo().compareTo(BigDecimal.valueOf(333.33))),
                () -> Assertions.assertEquals(TipoContaEnum.CONTA_CORRENTE, contaLovelace.getTipoConta()),
                () -> Assertions.assertEquals("Lovelace", contaLovelace.getTitular())
        );

        Conta contaBard = this.contaRepository.findByCpf("11122233344444");
        Assertions.assertAll(
                () -> Assertions.assertEquals(10L, contaBard.getId()),
                () -> Assertions.assertEquals(2L, contaBard.getAgencia()),
                () -> Assertions.assertEquals(2L, contaBard.getDigito()),
                () -> Assertions.assertEquals(0, contaBard.getSaldo().compareTo(BigDecimal.valueOf(55500.0))),
                () -> Assertions.assertEquals(TipoContaEnum.POUPANCA, contaBard.getTipoConta()),
                () -> Assertions.assertEquals("Bard", contaBard.getTitular())
        );

    }
}
