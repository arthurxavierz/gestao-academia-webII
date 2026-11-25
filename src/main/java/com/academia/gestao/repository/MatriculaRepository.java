package com.academia.gestao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academia.gestao.model.Matricula;
import com.academia.gestao.model.StatusMatricula;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    List<Matricula> findAllByOrderByIdDesc();

    boolean existsByAlunoIdAndStatus(Long alunoId, StatusMatricula status);

    boolean existsByAlunoId(Long alunoId);

    boolean existsByPlanoId(Long planoId);

    boolean existsByInstrutorId(Long instrutorId);

}
