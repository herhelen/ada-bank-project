package com.ada.banco.domain.usecase;

import com.ada.banco.domain.gateway.ContaGateway;
import com.ada.banco.domain.gateway.TransacaoGateway;
import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Service
public class RealizarTransferencia {

    private ContaGateway contaGateway;
    private TransacaoGateway transacaoGateway;

    public RealizarTransferencia(ContaGateway contaGateway, TransacaoGateway transacaoGateway) {
        this.contaGateway = contaGateway;
        this.transacaoGateway = transacaoGateway;
    }

    public Transacao execute(Transacao transacao) throws Exception {
        Conta contaOrigem = this.contaGateway.buscarPorAgenciaDigitoEConta(
                transacao.getConta().getAgencia(),
                transacao.getConta().getDigito(),
                transacao.getConta().getId());

        Conta contaDestino = this.contaGateway.buscarPorAgenciaDigitoEConta(
                transacao.getContaDestino().getAgencia(),
                transacao.getContaDestino().getDigito(),
                transacao.getContaDestino().getId());

        if (contaOrigem == null || contaDestino == null) {
            throw new Exception("Conta(s) inexistente(s) para realizar a transferência.");
        }

        if (transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("O valor da transferência deve ser maior que nulo.");
        }

        if (transacao.getValor().compareTo(contaOrigem.getSaldo()) > 0) {
            throw new Exception("A conta origem não possui saldo suficiente para realizar a transferência.");
        }

        // realizar a transferência e atualizar as contas
        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(transacao.getValor()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(transacao.getValor()));
        this.contaGateway.salvar(contaOrigem);
        this.contaGateway.salvar(contaDestino);

        // colocar a data e a hora da transação e salvá-la
        transacao.setDataHora(Date.from(Instant.now()));
        transacao.setConta(contaOrigem);
        transacao.setContaDestino(contaDestino);
        return this.transacaoGateway.salvar(transacao);
    }
}
