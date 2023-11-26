package com.ada.banco.infra.controller;

import com.ada.banco.domain.model.Transacao;
import com.ada.banco.domain.response.GenericResponse;
import com.ada.banco.domain.usecase.RealizarDeposito;
import com.ada.banco.domain.usecase.RealizarSaque;
import com.ada.banco.domain.usecase.RealizarTransferencia;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/bank-api/v1/transacao")
public class TransacaoController {

    private RealizarDeposito realizarDeposito;
    private RealizarSaque realizarSaque;
    private RealizarTransferencia realizarTransferencia;

    public TransacaoController(RealizarDeposito realizarDeposito, RealizarSaque realizarSaque,
                               RealizarTransferencia realizarTransferencia) {
        this.realizarDeposito = realizarDeposito;
        this.realizarSaque = realizarSaque;
        this.realizarTransferencia = realizarTransferencia;
    }

    @PostMapping("/depositar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponse> depositar(@RequestBody Transacao transacao) {
        try {
            Transacao deposito = this.realizarDeposito.execute(transacao);

            if (deposito != null) {
                GenericResponse response = new GenericResponse();
                response.setStatus(HttpStatus.OK.value());
                response.setData(deposito);
                response.setMessage("Depósito realizado com sucesso!");

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PostMapping("/sacar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponse> sacar(@RequestBody Transacao transacao) {
        try {
            Transacao saque = this.realizarSaque.execute(transacao);

            if (saque != null) {
                GenericResponse response = new GenericResponse();
                response.setStatus(HttpStatus.OK.value());
                response.setData(saque);
                response.setMessage("Saque realizado com sucesso!");

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PostMapping("/transferir")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GenericResponse> transferir(@RequestBody Transacao transacao) {
        try {
            Transacao trasferencia = this.realizarTransferencia.execute(transacao);

            if (trasferencia != null) {
                GenericResponse response = new GenericResponse();
                response.setStatus(HttpStatus.OK.value());
                response.setData(trasferencia);
                response.setMessage("Transferência realizada com sucesso!");

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }
}
