package com.ada.banco.domain.usecase;

import com.ada.banco.domain.gateway.ContaGateway;
import com.ada.banco.domain.gateway.TransacaoGateway;
import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MostrarExtratoConta {

    private ContaGateway contaGateway;
    private TransacaoGateway transacaoGateway;

    public MostrarExtratoConta(ContaGateway contaGateway, TransacaoGateway transacaoGateway) {
        this.contaGateway = contaGateway;
        this.transacaoGateway = transacaoGateway;
    }

    public List<Transacao> execute(Conta conta) throws Exception {
        Conta contaBuscada = this.contaGateway.buscarPorAgenciaDigitoEConta(
                conta.getAgencia(), conta.getDigito(), conta.getId());

        if(contaBuscada == null) {
            throw new Exception("Conta inexistente para mostrar o extrato.");
        }

        return this.transacaoGateway.buscaPorConta(contaBuscada);
    }
}
