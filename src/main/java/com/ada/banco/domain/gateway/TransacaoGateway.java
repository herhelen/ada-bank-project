package com.ada.banco.domain.gateway;

import com.ada.banco.domain.model.Transacao;

import java.util.List;

public interface TransacaoGateway {
    List<Transacao> buscarPorCpf(String cpf);
    Transacao salvar(Transacao transacao);
}
