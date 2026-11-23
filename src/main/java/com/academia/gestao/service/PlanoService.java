package com.academia.gestao.service;

import java.util.List;

import com.academia.gestao.model.Plano;

public interface PlanoService {

    List<Plano> getAllPlanos();

    Plano savePlano(Plano plano);

    Plano getPlanoById(Long id);

    Plano updatePlano(Long id, Plano plano);

    void deletePlano(Long id);

}
