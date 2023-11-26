package com.ada.banco.infra.gateway.bd;

import com.ada.banco.domain.model.Conta;
import com.ada.banco.domain.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findAllByConta(Conta conta);

    @Query("SELECT t FROM Transacao t WHERE t.conta.id = :#{#conta.id} OR t.contaDestino.id = :#{#conta.id} ORDER BY t.id")
    List<Transacao> findAllByContaCustom(@Param("conta") Conta conta);
}
