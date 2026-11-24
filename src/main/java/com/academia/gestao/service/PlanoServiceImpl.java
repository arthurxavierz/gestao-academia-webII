package com.academia.gestao.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Modalidade;
import com.academia.gestao.model.Plano;
import com.academia.gestao.repository.ModalidadeRepository;
import com.academia.gestao.repository.PlanoRepository;

@Service
public class PlanoServiceImpl implements PlanoService {

    private final PlanoRepository planoRepository;
    private final ModalidadeRepository modalidadeRepository;

    public PlanoServiceImpl(PlanoRepository planoRepository, ModalidadeRepository modalidadeRepository) {
        this.planoRepository = planoRepository;
        this.modalidadeRepository = modalidadeRepository;
    }

    @Override
    public List<Plano> getAllPlanos() {
        return planoRepository.findAll();
    }

    @Override
    public List<Modalidade> getAllModalidades() {
        return modalidadeRepository.findAll();
    }

    @Override
    public Plano savePlano(Plano plano, List<Long> modalidadeIds) {
        plano.setModalidades(buscarModalidades(modalidadeIds));
        return planoRepository.save(plano);
    }

    @Override
    public Plano getPlanoById(Long id) {
        return planoRepository.findById(id).orElse(null);
    }

    @Override
    public Plano updatePlano(Long id, Plano planoAtualizado, List<Long> modalidadeIds) {
        Plano plano = planoRepository.findById(id).orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        plano.setNome(planoAtualizado.getNome());
        plano.setValor(planoAtualizado.getValor());
        plano.setDuracaoDias(planoAtualizado.getDuracaoDias());
        plano.setAtivo(planoAtualizado.isAtivo());
        plano.setModalidades(buscarModalidades(modalidadeIds));

        return planoRepository.save(plano);
    }

    @Override
    public void deletePlano(Long id) {
        planoRepository.deleteById(id);
    }

    private List<Modalidade> buscarModalidades(List<Long> modalidadeIds) {
        if (modalidadeIds == null || modalidadeIds.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(modalidadeRepository.findAllById(modalidadeIds));
    }

}
