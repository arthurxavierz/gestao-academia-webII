package com.academia.gestao.service;

import java.util.List;

import com.academia.gestao.model.Instrutor;
import com.academia.gestao.model.Modalidade;

public interface InstrutorService {

    List<Instrutor> getAllInstrutores();

    List<Modalidade> getAllModalidades();

    Instrutor saveInstrutor(Instrutor instrutor, List<Long> modalidadeIds);

    Instrutor getInstrutorById(Long id);

    Instrutor updateInstrutor(Long id, Instrutor instrutor, List<Long> modalidadeIds);

    void deleteInstrutor(Long id);

}
