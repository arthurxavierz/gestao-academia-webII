package com.academia.gestao.service;

import java.util.List;

import com.academia.gestao.model.Modalidade;

public interface ModalidadeService {

    List<Modalidade> getAllModalidades();

    Modalidade saveModalidade(Modalidade modalidade);

    Modalidade getModalidadeById(Long id);

    Modalidade updateModalidade(Long id, Modalidade modalidade);

    void deleteModalidade(Long id);

}
