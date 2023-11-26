package com.ada.banco.infra.gateway.bd;

import com.ada.banco.domain.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Conta findByCpf(String cpf);

    Conta findByAgenciaAndDigitoAndId(Long agencia, Long digito, Long id);

    List<Conta> findAll();
}
