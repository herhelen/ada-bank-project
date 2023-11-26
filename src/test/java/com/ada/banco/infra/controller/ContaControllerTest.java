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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts="../../scripts/truncate_tables_cleanup.sql",
        executionPhase=Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

    @Test
    @Sql(scripts="../../scripts/insert_contas.sql",
            executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void listarTodasContas_DeveRetornarStatus200() throws Exception {

        // todo: rever esse teste
        // when
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/bank-api/v1/contas"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // then
        Conta lovelaceConta = this.contaRepository.findByCpf("11122233344488");
        Assertions.assertAll(
                () -> Assertions.assertEquals(4L, lovelaceConta.getId()),
                () -> Assertions.assertEquals(1L, lovelaceConta.getAgencia()),
                () -> Assertions.assertEquals(1L, lovelaceConta.getDigito()),
                () -> Assertions.assertEquals(0, lovelaceConta.getSaldo().compareTo(BigDecimal.valueOf(333.33))),
                () -> Assertions.assertEquals(TipoContaEnum.CONTA_CORRENTE, lovelaceConta.getTipoConta()),
                () -> Assertions.assertEquals("Lovelace", lovelaceConta.getTitular())
        );

        Conta bardConta = this.contaRepository.findByCpf("11122233344444");
        Assertions.assertAll(
                () -> Assertions.assertEquals(10L, bardConta.getId()),
                () -> Assertions.assertEquals(2L, bardConta.getAgencia()),
                () -> Assertions.assertEquals(2L, bardConta.getDigito()),
                () -> Assertions.assertEquals(0, bardConta.getSaldo().compareTo(BigDecimal.valueOf(55500.0))),
                () -> Assertions.assertEquals(TipoContaEnum.POUPANCA, bardConta.getTipoConta()),
                () -> Assertions.assertEquals("Bard", bardConta.getTitular())
        );
    }

    @Test
    @Sql(scripts={"../../scripts/insert_contas.sql", "../../scripts/insert_transacoes.sql",
            "../../scripts/update_saldos.sql"},
            executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void mostrarExtratoConta_DeveRetornarStatus200() throws Exception {

        String resultadoEsperado = "[\n" +
                "    {\n" +
                "        \"id\": 1,\n" +
                "        \"dataHora\": \"2023-11-25T13:12:23.651+00:00\",\n" +
                "        \"conta\": {\n" +
                "            \"id\": 2,\n" +
                "            \"agencia\": 1,\n" +
                "            \"digito\": 1,\n" +
                "            \"saldo\": 619.45,\n" +
                "            \"titular\": \"Ada 2\",\n" +
                "            \"cpf\": \"11122233344466\",\n" +
                "            \"tipoConta\": \"POUPANCA\"\n" +
                "        },\n" +
                "        \"contaDestino\": null,\n" +
                "        \"valor\": 200.00,\n" +
                "        \"tipoTransacao\": \"SAQUE\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 2,\n" +
                "        \"dataHora\": \"2023-11-26T09:12:13.894+00:00\",\n" +
                "        \"conta\": {\n" +
                "            \"id\": 2,\n" +
                "            \"agencia\": 1,\n" +
                "            \"digito\": 1,\n" +
                "            \"saldo\": 619.45,\n" +
                "            \"titular\": \"Ada 2\",\n" +
                "            \"cpf\": \"11122233344466\",\n" +
                "            \"tipoConta\": \"POUPANCA\"\n" +
                "        },\n" +
                "        \"contaDestino\": null,\n" +
                "        \"valor\": 1320.00,\n" +
                "        \"tipoTransacao\": \"DEPOSITO\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 3,\n" +
                "        \"dataHora\": \"2023-11-26T09:12:28.911+00:00\",\n" +
                "        \"conta\": {\n" +
                "            \"id\": 2,\n" +
                "            \"agencia\": 1,\n" +
                "            \"digito\": 1,\n" +
                "            \"saldo\": 619.45,\n" +
                "            \"titular\": \"Ada 2\",\n" +
                "            \"cpf\": \"11122233344466\",\n" +
                "            \"tipoConta\": \"POUPANCA\"\n" +
                "        },\n" +
                "        \"contaDestino\": {\n" +
                "            \"id\": 6,\n" +
                "            \"agencia\": 2,\n" +
                "            \"digito\": 1,\n" +
                "            \"saldo\": 0.55,\n" +
                "            \"titular\": \"Lovelace 2\",\n" +
                "            \"cpf\": \"11122233344400\",\n" +
                "            \"tipoConta\": \"POUPANCA\"\n" +
                "        },\n" +
                "        \"valor\": 1000.55,\n" +
                "        \"tipoTransacao\": \"TRANSFERENCIA\"\n" +
                "    }\n" +
                "]";

        // when
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/bank-api/v1/contas/extrato")
                        .queryParam("agencia", "1")
                        .queryParam("digito", "1")
                        .queryParam("conta", "2")
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().json(resultadoEsperado)
                );
    }

    @Test
    public void mostrarExtratoConta_ContaInexistente_DeveRetornarStatus400() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/bank-api/v1/contas/extrato")
                        .queryParam("agencia", "1")
                        .queryParam("digito", "1")
                        .queryParam("conta", "666")
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.status().reason("Não é possível mostrar o extrato de uma conta inexistente.")
                );
    }

    @Test
    @Sql(scripts={"../../scripts/insert_contas.sql", "../../scripts/insert_transacoes.sql",
            "../../scripts/update_saldos.sql"},
            executionPhase=Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void mostrarExtratoConta_ContaDestino_DeveRetornarStatus200() throws Exception {

        String resultadoEsperado = "[\n" +
                "    {\n" +
                "        \"id\": 3,\n" +
                "        \"dataHora\": \"2023-11-26T09:12:28.911+00:00\",\n" +
                "        \"conta\": {\n" +
                "            \"id\": 2,\n" +
                "            \"agencia\": 1,\n" +
                "            \"digito\": 1,\n" +
                "            \"saldo\": 619.45,\n" +
                "            \"titular\": \"Ada 2\",\n" +
                "            \"cpf\": \"11122233344466\",\n" +
                "            \"tipoConta\": \"POUPANCA\"\n" +
                "        },\n" +
                "        \"contaDestino\": {\n" +
                "            \"id\": 6,\n" +
                "            \"agencia\": 2,\n" +
                "            \"digito\": 1,\n" +
                "            \"saldo\": 0.55,\n" +
                "            \"titular\": \"Lovelace 2\",\n" +
                "            \"cpf\": \"11122233344400\",\n" +
                "            \"tipoConta\": \"POUPANCA\"\n" +
                "        },\n" +
                "        \"valor\": 1000.55,\n" +
                "        \"tipoTransacao\": \"TRANSFERENCIA\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 4,\n" +
                "        \"dataHora\": \"2023-11-26T09:23:13.894+00:00\",\n" +
                "        \"conta\": {\n" +
                "            \"id\": 6,\n" +
                "            \"agencia\": 2,\n" +
                "            \"digito\": 1,\n" +
                "            \"saldo\": 0.55,\n" +
                "            \"titular\": \"Lovelace 2\",\n" +
                "            \"cpf\": \"11122233344400\",\n" +
                "            \"tipoConta\": \"POUPANCA\"\n" +
                "        },\n" +
                "        \"contaDestino\": null,\n" +
                "        \"valor\": 1000.00,\n" +
                "        \"tipoTransacao\": \"SAQUE\"\n" +
                "    }\n" +
                "]";

        // when
        this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/bank-api/v1/contas/extrato")
                        .queryParam("agencia", "2")
                        .queryParam("digito", "1")
                        .queryParam("conta", "6")
                )
                .andExpectAll(
                        MockMvcResultMatchers.status().isOk(),
                        MockMvcResultMatchers.content().json(resultadoEsperado)
                );
    }
}