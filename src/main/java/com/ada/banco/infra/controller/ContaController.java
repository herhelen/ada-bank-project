package com.ada.banco.infra.controller;

import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.response.GenericResponse;
import com.ada.banco.domain.usecase.CriarNovaConta;
import com.ada.banco.domain.usecase.ListarTodasContas;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/bank-api/v1/contas")
public class ContaController {

    private CriarNovaConta criarNovaConta;
    private ListarTodasContas listarTodasContas;

    public ContaController(CriarNovaConta criarNovaConta, ListarTodasContas listarTodasContas) {
        this.criarNovaConta = criarNovaConta;
        this.listarTodasContas = listarTodasContas;
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Conta> listarTodasContas() {
        return this.listarTodasContas.execute();
    }

    /*
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Usuario> buscarTodosOsUsuarios() {
        try {
            return usuarioService.listarTodosOsUsuarios();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao buscar todos os usuários");
        }
    }

     */


}
