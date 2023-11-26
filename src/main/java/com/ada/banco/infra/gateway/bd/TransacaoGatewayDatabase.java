package com.ada.banco.infra.gateway.bd;

import com.ada.banco.domain.gateway.TransacaoGateway;
import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransacaoGatewayDatabase implements TransacaoGateway {

    private final TransacaoRepository transacaoRepository;

    public TransacaoGatewayDatabase(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    @Override
    public List<Transacao> buscaPorConta(Conta conta) {
        return this.transacaoRepository.findAllByConta(conta);
    }

    @Override
    public Transacao salvar(Transacao transacao) {
        return this.transacaoRepository.save(transacao);
    }
}
