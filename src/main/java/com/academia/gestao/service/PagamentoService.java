package com.academia.gestao.service;

import java.util.List;

import com.academia.gestao.model.Cobranca;
import com.academia.gestao.model.Pagamento;

public interface PagamentoService {

    List<Pagamento> getAllPagamentos();

    Cobranca getCobrancaById(Long id);

    Pagamento registrarPagamento(Pagamento pagamento) throws IllegalStateException;

}
