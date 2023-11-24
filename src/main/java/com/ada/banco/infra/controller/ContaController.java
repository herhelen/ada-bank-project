package com.ada.banco.infra.controller;

import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.response.GenericResponse;
import com.ada.banco.domain.usecase.CriarNovaConta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/bank-api/v1/contas")
public class ContaController {

    private CriarNovaConta criarNovaConta;

    public ContaController(CriarNovaConta criarNovaConta) {
        this.criarNovaConta = criarNovaConta;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GenericResponse> criarConta(@RequestBody Conta conta) {
        try {
            Conta novaConta = this.criarNovaConta.execute(conta);

            if (novaConta != null) {
                GenericResponse response = new GenericResponse();
                response.setStatus(HttpStatus.CREATED.value());
                response.setData(novaConta);
                response.setMessage("Nova conta criada com sucesso!");

                return new ResponseEntity<>(response, HttpStatus.CREATED);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao criar nova conta");
        }
    }

}
