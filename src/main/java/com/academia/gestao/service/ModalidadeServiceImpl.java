package com.academia.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Modalidade;
import com.academia.gestao.repository.ModalidadeRepository;
import com.academia.gestao.repository.PlanoRepository;

@Service
public class ModalidadeServiceImpl implements ModalidadeService {

    private final ModalidadeRepository modalidadeRepository;
    private final PlanoRepository planoRepository;

    public ModalidadeServiceImpl(ModalidadeRepository modalidadeRepository, PlanoRepository planoRepository) {
        this.modalidadeRepository = modalidadeRepository;
        this.planoRepository = planoRepository;
    }

    @Override
    public List<Modalidade> getAllModalidades() {
        return modalidadeRepository.findAll();
    }

    @Override
    public Modalidade saveModalidade(Modalidade modalidade) {
        return modalidadeRepository.save(modalidade);
    }

    @Override
    public Modalidade getModalidadeById(Long id) {
        return modalidadeRepository.findById(id).orElse(null);
    }

    @Override
    public Modalidade updateModalidade(Long id, Modalidade modalidadeAtualizada) {
        Modalidade modalidade = modalidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modalidade não encontrada"));

        modalidade.setNome(modalidadeAtualizada.getNome());
        modalidade.setDescricao(modalidadeAtualizada.getDescricao());

        return modalidadeRepository.save(modalidade);
    }

    @Override
    public void deleteModalidade(Long id) {
        if (planoRepository.existsByModalidadesId(id)) {
            throw new IllegalStateException("Esta modalidade está em um plano e não pode ser excluída.");
        }
        modalidadeRepository.deleteById(id);
    }

}
