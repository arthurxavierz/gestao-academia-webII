package com.academia.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Plano;
import com.academia.gestao.repository.PlanoRepository;

@Service
public class PlanoServiceImpl implements PlanoService {

    private final PlanoRepository planoRepository;

    public PlanoServiceImpl(PlanoRepository planoRepository) {
        this.planoRepository = planoRepository;
    }

    @Override
    public List<Plano> getAllPlanos() {
        return planoRepository.findAll();
    }

    @Override
    public Plano savePlano(Plano plano) {
        return planoRepository.save(plano);
    }

    @Override
    public Plano getPlanoById(Long id) {
        return planoRepository.findById(id).orElse(null);
    }

    @Override
    public Plano updatePlano(Long id, Plano planoAtualizado) {
        Plano plano = planoRepository.findById(id).orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        plano.setNome(planoAtualizado.getNome());
        plano.setValor(planoAtualizado.getValor());
        plano.setDuracaoDias(planoAtualizado.getDuracaoDias());
        plano.setAtivo(planoAtualizado.isAtivo());

        return planoRepository.save(plano);
    }

    @Override
    public void deletePlano(Long id) {
        planoRepository.deleteById(id);
    }

}
