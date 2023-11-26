package com.ada.banco.domain.gateway;

import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;

import java.util.List;

public interface TransacaoGateway {
    List<Transacao> buscaPorConta(Conta conta);
    Transacao salvar(Transacao transacao);
}
