package com.ada.banco.domain.usecase;

import com.ada.banco.domain.gateway.ContaGateway;
import com.ada.banco.domain.model.Conta;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarTodasContas {

    private ContaGateway contaGateway;

    public ListarTodasContas(ContaGateway contaGateway) {
        this.contaGateway = contaGateway;
    }

    public List<Conta> execute() {
        return this.contaGateway.buscarTodasContas();
    }
}
