package com.academia.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Aluno;
import com.academia.gestao.repository.AlunoRepository;
import com.academia.gestao.repository.MatriculaRepository;

@Service
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository;

    public AlunoServiceImpl(AlunoRepository alunoRepository, MatriculaRepository matriculaRepository) {
        this.alunoRepository = alunoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    public List<Aluno> getAllAlunos() {
        return alunoRepository.findAll();
    }

    @Override
    public List<Aluno> buscarAlunos(String busca) {
        if (busca == null || busca.isBlank()) {
            return alunoRepository.findAll();
        }
        return alunoRepository.findByNomeContainingIgnoreCaseOrCpfContaining(busca.trim(), busca.trim());
    }

    @Override
    public Aluno saveAluno(Aluno aluno) {
        if (alunoRepository.existsByCpf(aluno.getCpf())) {
            throw new IllegalStateException("Já existe um aluno com este CPF.");
        }
        return alunoRepository.save(aluno);
    }

    @Override
    public Aluno getAlunoById(Long id) {
        return alunoRepository.findById(id).orElse(null);
    }

    @Override
    public Aluno updateAluno(Long id, Aluno alunoAtualizado) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (alunoRepository.existsByCpfAndIdNot(alunoAtualizado.getCpf(), id)) {
            throw new IllegalStateException("Já existe um aluno com este CPF.");
        }

        aluno.setNome(alunoAtualizado.getNome());
        aluno.setCpf(alunoAtualizado.getCpf());
        aluno.setEmail(alunoAtualizado.getEmail());
        aluno.setTelefone(alunoAtualizado.getTelefone());
        aluno.setDataNascimento(alunoAtualizado.getDataNascimento());

        return alunoRepository.save(aluno);
    }

    @Override
    public void deleteAluno(Long id) {
        if (matriculaRepository.existsByAlunoId(id)) {
            throw new IllegalStateException("Este aluno possui matrícula e não pode ser excluído.");
        }
        alunoRepository.deleteById(id);
    }

}
