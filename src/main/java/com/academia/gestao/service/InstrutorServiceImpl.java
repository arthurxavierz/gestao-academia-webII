package com.academia.gestao.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Instrutor;
import com.academia.gestao.model.Modalidade;
import com.academia.gestao.repository.InstrutorRepository;
import com.academia.gestao.repository.MatriculaRepository;
import com.academia.gestao.repository.ModalidadeRepository;

@Service
public class InstrutorServiceImpl implements InstrutorService {

    private final InstrutorRepository instrutorRepository;
    private final ModalidadeRepository modalidadeRepository;
    private final MatriculaRepository matriculaRepository;

    public InstrutorServiceImpl(InstrutorRepository instrutorRepository, ModalidadeRepository modalidadeRepository,
            MatriculaRepository matriculaRepository) {
        this.instrutorRepository = instrutorRepository;
        this.modalidadeRepository = modalidadeRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    public List<Instrutor> getAllInstrutores() {
        return instrutorRepository.findAll();
    }

    @Override
    public List<Modalidade> getAllModalidades() {
        return modalidadeRepository.findAll();
    }

    @Override
    public Instrutor saveInstrutor(Instrutor instrutor, List<Long> modalidadeIds) {
        instrutor.setModalidades(buscarModalidades(modalidadeIds));
        return instrutorRepository.save(instrutor);
    }

    @Override
    public Instrutor getInstrutorById(Long id) {
        return instrutorRepository.findById(id).orElse(null);
    }

    @Override
    public Instrutor updateInstrutor(Long id, Instrutor instrutorAtualizado, List<Long> modalidadeIds) {
        Instrutor instrutor = instrutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Instrutor não encontrado"));

        instrutor.setNome(instrutorAtualizado.getNome());
        instrutor.setCref(instrutorAtualizado.getCref());
        instrutor.setEspecialidade(instrutorAtualizado.getEspecialidade());
        instrutor.setModalidades(buscarModalidades(modalidadeIds));

        return instrutorRepository.save(instrutor);
    }

    @Override
    public void deleteInstrutor(Long id) {
        if (matriculaRepository.existsByInstrutorId(id)) {
            throw new IllegalStateException("Este instrutor acompanha alunos matriculados e não pode ser excluído.");
        }
        instrutorRepository.deleteById(id);
    }

    private List<Modalidade> buscarModalidades(List<Long> modalidadeIds) {
        if (modalidadeIds == null || modalidadeIds.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(modalidadeRepository.findAllById(modalidadeIds));
    }

}
