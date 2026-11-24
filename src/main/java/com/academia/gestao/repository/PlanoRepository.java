package com.academia.gestao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academia.gestao.model.Plano;

public interface PlanoRepository extends JpaRepository<Plano, Long> {

    boolean existsByModalidadesId(Long modalidadeId);

}
