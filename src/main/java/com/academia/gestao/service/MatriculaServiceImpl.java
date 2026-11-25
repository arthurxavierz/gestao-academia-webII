package com.academia.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Aluno;
import com.academia.gestao.model.Instrutor;
import com.academia.gestao.model.Matricula;
import com.academia.gestao.model.Plano;
import com.academia.gestao.model.StatusMatricula;
import com.academia.gestao.repository.AlunoRepository;
import com.academia.gestao.repository.InstrutorRepository;
import com.academia.gestao.repository.MatriculaRepository;
import com.academia.gestao.repository.PlanoRepository;

@Service
public class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final PlanoRepository planoRepository;
    private final InstrutorRepository instrutorRepository;

    public MatriculaServiceImpl(MatriculaRepository matriculaRepository, AlunoRepository alunoRepository,
            PlanoRepository planoRepository, InstrutorRepository instrutorRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.planoRepository = planoRepository;
        this.instrutorRepository = instrutorRepository;
    }

    @Override
    public List<Matricula> getAllMatriculas() {
        return matriculaRepository.findAllByOrderByIdDesc();
    }

    @Override
    public List<Aluno> getAllAlunos() {
        return alunoRepository.findAll();
    }

    @Override
    public List<Plano> getPlanosAtivos() {
        return planoRepository.findByAtivoTrue();
    }

    @Override
    public List<Instrutor> getAllInstrutores() {
        return instrutorRepository.findAll();
    }

    @Override
    public Matricula saveMatricula(Matricula matricula) throws IllegalStateException {
        if (matricula.getAluno() == null || matricula.getAluno().getId() == null) {
            throw new IllegalStateException("Escolha um aluno.");
        }
        if (matricula.getPlano() == null || matricula.getPlano().getId() == null) {
            throw new IllegalStateException("Escolha um plano.");
        }
        if (matricula.getInstrutor() == null || matricula.getInstrutor().getId() == null) {
            throw new IllegalStateException("Escolha um instrutor.");
        }

        Aluno aluno = alunoRepository.findById(matricula.getAluno().getId())
                .orElseThrow(() -> new IllegalStateException("Aluno não encontrado."));
        Plano plano = planoRepository.findById(matricula.getPlano().getId())
                .orElseThrow(() -> new IllegalStateException("Plano não encontrado."));
        Instrutor instrutor = instrutorRepository.findById(matricula.getInstrutor().getId())
                .orElseThrow(() -> new IllegalStateException("Instrutor não encontrado."));

        if (!plano.isAtivo()) {
            throw new IllegalStateException("O plano " + plano.getNome() + " está inativo.");
        }
        if (matriculaRepository.existsByAlunoIdAndStatus(aluno.getId(), StatusMatricula.ATIVA)) {
            throw new IllegalStateException("O aluno " + aluno.getNome() + " já possui uma matrícula ativa.");
        }

        matricula.setAluno(aluno);
        matricula.setPlano(plano);
        matricula.setInstrutor(instrutor);
        matricula.setDataFim(matricula.getDataInicio().plusDays(plano.getDuracaoDias()));
        matricula.setStatus(StatusMatricula.ATIVA);

        return matriculaRepository.save(matricula);
    }

    @Override
    public Matricula getMatriculaById(Long id) {
        return matriculaRepository.findById(id).orElse(null);
    }

    @Override
    public void cancelarMatricula(Long id) throws IllegalStateException {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Matrícula não encontrada."));

        if (matricula.getStatus() == StatusMatricula.CANCELADA) {
            throw new IllegalStateException("Esta matrícula já está cancelada.");
        }

        matricula.setStatus(StatusMatricula.CANCELADA);
        matriculaRepository.save(matricula);
    }

}
