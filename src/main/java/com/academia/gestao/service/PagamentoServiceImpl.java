package com.academia.gestao.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.academia.gestao.model.Cobranca;
import com.academia.gestao.model.Pagamento;
import com.academia.gestao.model.StatusCobranca;
import com.academia.gestao.repository.CobrancaRepository;
import com.academia.gestao.repository.PagamentoRepository;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final CobrancaRepository cobrancaRepository;

    public PagamentoServiceImpl(PagamentoRepository pagamentoRepository, CobrancaRepository cobrancaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.cobrancaRepository = cobrancaRepository;
    }

    @Override
    public List<Pagamento> getAllPagamentos() {
        return pagamentoRepository.findAllByOrderByIdDesc();
    }

    @Override
    public Cobranca getCobrancaById(Long id) {
        return cobrancaRepository.findById(id).orElse(null);
    }

    @Override
    public Pagamento registrarPagamento(Pagamento pagamento) throws IllegalStateException {
        if (pagamento.getCobranca() == null || pagamento.getCobranca().getId() == null) {
            throw new IllegalStateException("Escolha a mensalidade.");
        }

        Cobranca cobranca = cobrancaRepository.findById(pagamento.getCobranca().getId())
                .orElseThrow(() -> new IllegalStateException("Mensalidade não encontrada."));

        if (cobranca.getStatus() == StatusCobranca.PAGA) {
            throw new IllegalStateException("Esta mensalidade já foi paga.");
        }

        cobranca.setStatus(StatusCobranca.PAGA);
        cobrancaRepository.save(cobranca);

        pagamento.setCobranca(cobranca);
        pagamento.setValorPago(cobranca.getValor());
        pagamento.setDataPagamento(LocalDate.now());
        return pagamentoRepository.save(pagamento);
    }

}
