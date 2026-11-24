package com.academia.gestao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academia.gestao.model.Instrutor;

public interface InstrutorRepository extends JpaRepository<Instrutor, Long> {

    boolean existsByModalidadesId(Long modalidadeId);

}
