package com.academia.gestao.service;

import java.util.List;

import com.academia.gestao.model.Modalidade;
import com.academia.gestao.model.Plano;

public interface PlanoService {

    List<Plano> getAllPlanos();

    List<Modalidade> getAllModalidades();

    Plano savePlano(Plano plano, List<Long> modalidadeIds);

    Plano getPlanoById(Long id);

    Plano updatePlano(Long id, Plano plano, List<Long> modalidadeIds);

    void deletePlano(Long id);

}
