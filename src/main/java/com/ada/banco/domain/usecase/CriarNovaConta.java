package com.ada.banco.domain.usecase;

import com.ada.banco.domain.gateway.ContaGateway;
import com.ada.banco.domain.model.Conta;
import org.springframework.stereotype.Service;

@Service
public class CriarNovaConta {

    private ContaGateway contaGateway;

    public CriarNovaConta(ContaGateway contaGateway) {
        this.contaGateway = contaGateway;
    }

    public Conta execute(Conta conta) throws Exception {
        if(this.contaGateway.buscarPorCpf(conta.getCpf()) != null) {
            throw new Exception("Usuário já possui uma conta.");
        }

        return this.contaGateway.salvar(conta);
    }
}
