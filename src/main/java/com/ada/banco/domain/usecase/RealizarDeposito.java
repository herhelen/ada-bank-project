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
public class RealizarDeposito {

    private ContaGateway contaGateway;
    private TransacaoGateway transacaoGateway;

    public RealizarDeposito(ContaGateway contaGateway, TransacaoGateway transacaoGateway) {
        this.contaGateway = contaGateway;
        this.transacaoGateway = transacaoGateway;
    }

    public Transacao execute(Transacao transacao) throws Exception {
        Conta conta = this.contaGateway.buscarPorAgenciaDigitoEConta(
                transacao.getConta().getAgencia(),
                transacao.getConta().getDigito(),
                transacao.getConta().getId());

        if(conta == null) {
            throw new Exception("Conta inexistente para realizar o depósito.");
        }

        if(transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("O valor do depósito deve ser maior que nulo.");
        }

        // realizar o depósito e atualizar a conta
        conta.setSaldo(conta.getSaldo().add(transacao.getValor()));
        this.contaGateway.salvar(conta);

        // colocar a data e a hora da transação e salvá-la
        transacao.setDataHora(Date.from(Instant.now()));
        transacao.setConta(conta);
        return this.transacaoGateway.salvar(transacao);
    }
}
