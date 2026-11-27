package com.academia.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Cobranca;
import com.academia.gestao.repository.CobrancaRepository;

@Service
public class CobrancaServiceImpl implements CobrancaService {

    private final CobrancaRepository cobrancaRepository;

    public CobrancaServiceImpl(CobrancaRepository cobrancaRepository) {
        this.cobrancaRepository = cobrancaRepository;
    }

    @Override
    public List<Cobranca> getAllCobrancas() {
        return cobrancaRepository.findAllByOrderByDataVencimentoAsc();
    }

}
