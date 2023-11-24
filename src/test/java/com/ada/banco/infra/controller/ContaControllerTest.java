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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;

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
}
