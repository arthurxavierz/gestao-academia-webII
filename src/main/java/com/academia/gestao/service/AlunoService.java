package com.academia.gestao.service;

import java.util.List;

import com.academia.gestao.model.Aluno;

public interface AlunoService {

    List<Aluno> getAllAlunos();

    List<Aluno> buscarAlunos(String busca);

    Aluno saveAluno(Aluno aluno);

    Aluno getAlunoById(Long id);

    Aluno updateAluno(Long id, Aluno aluno);

    void deleteAluno(Long id);

}
