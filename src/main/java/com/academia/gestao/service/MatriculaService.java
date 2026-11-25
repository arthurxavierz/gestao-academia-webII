package com.academia.gestao.service;

import java.util.List;

import com.academia.gestao.model.Aluno;
import com.academia.gestao.model.Matricula;
import com.academia.gestao.model.Plano;

public interface MatriculaService {

    List<Matricula> getAllMatriculas();

    List<Aluno> getAllAlunos();

    List<Plano> getPlanosAtivos();

    Matricula saveMatricula(Matricula matricula) throws IllegalStateException;

    Matricula getMatriculaById(Long id);

    void cancelarMatricula(Long id) throws IllegalStateException;

}
