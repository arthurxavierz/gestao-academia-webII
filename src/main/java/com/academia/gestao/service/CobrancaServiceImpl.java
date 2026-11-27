package com.academia.gestao.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Cobranca;
import com.academia.gestao.model.StatusCobranca;
import com.academia.gestao.repository.CobrancaRepository;

@Service
public class CobrancaServiceImpl implements CobrancaService {

    private final CobrancaRepository cobrancaRepository;

    public CobrancaServiceImpl(CobrancaRepository cobrancaRepository) {
        this.cobrancaRepository = cobrancaRepository;
    }

    @Override
    public List<Cobranca> getAllCobrancas() {
        marcarAtrasadas();
        return cobrancaRepository.findAllByOrderByDataVencimentoAsc();
    }

    private void marcarAtrasadas() {
        List<Cobranca> vencidas = cobrancaRepository.findByStatusAndDataVencimentoBefore(StatusCobranca.ABERTA,
                LocalDate.now());

        for (Cobranca cobranca : vencidas) {
            cobranca.setStatus(StatusCobranca.ATRASADA);
        }
        cobrancaRepository.saveAll(vencidas);
    }

}
