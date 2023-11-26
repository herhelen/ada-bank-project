package com.ada.banco.domain.gateway;

import com.ada.banco.domain.model.Conta;

import java.util.List;

public interface ContaGateway {
    Conta buscarPorCpf(String cpf);
    Conta buscarPorAgenciaDigitoEConta(Long agencia, Long digito, Long contaId);
    List<Conta> buscarTodasContas();
    Conta salvar(Conta conta);
}
