package com.academia.gestao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.gestao.model.Cobranca;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {

    List<Cobranca> findAllByOrderByDataVencimentoAsc();

}
