package com.academia.gestao.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.gestao.model.Cobranca;
import com.academia.gestao.model.StatusCobranca;

@Repository
public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {

    List<Cobranca> findAllByOrderByDataVencimentoAsc();

    List<Cobranca> findByStatusAndDataVencimentoBefore(StatusCobranca status, LocalDate data);

}
