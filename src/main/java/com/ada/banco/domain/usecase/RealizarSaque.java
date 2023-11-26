package com.ada.banco.domain.usecase;

import com.ada.banco.domain.gateway.ContaGateway;
import com.ada.banco.domain.gateway.TransacaoGateway;
import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import com.ada.banco.domain.model.enums.TipoTransacaoEnum;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Service
public class RealizarSaque {

    private ContaGateway contaGateway;
    private TransacaoGateway transacaoGateway;

    public RealizarSaque(ContaGateway contaGateway, TransacaoGateway transacaoGateway) {
        this.contaGateway = contaGateway;
        this.transacaoGateway = transacaoGateway;
    }

    public Transacao execute(Transacao transacao) throws Exception {
        Conta conta = this.contaGateway.buscarPorAgenciaDigitoEConta(
                transacao.getConta().getAgencia(),
                transacao.getConta().getDigito(),
                transacao.getConta().getId());

        if (conta == null) {
            throw new Exception("Conta inexistente para realizar o saque.");
        }

        if (transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("O valor do saque deve ser maior que nulo.");
        }

        if (transacao.getValor().compareTo(conta.getSaldo()) > 0) {
            throw new Exception("A conta não possui saldo suficiente para realizar o saque.");
        }

        // realizar o saque e atualizar a conta
        conta.setSaldo(conta.getSaldo().subtract(transacao.getValor()));
        this.contaGateway.salvar(conta);

        // colocar a data e a hora e o tipo da transação e salvá-la
        transacao.setDataHora(Date.from(Instant.now()));
        transacao.setConta(conta);
        transacao.setTipoTransacao(TipoTransacaoEnum.SAQUE);
        return this.transacaoGateway.salvar(transacao);
    }
}
