package com.academia.gestao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.academia.gestao.model.Cobranca;
import com.academia.gestao.model.Pagamento;
import com.academia.gestao.model.StatusCobranca;
import com.academia.gestao.service.PagamentoService;

import jakarta.validation.Valid;

@Controller
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping("/pagamentos")
    public String index(Model model) {
        model.addAttribute("pagamentos", pagamentoService.getAllPagamentos());
        return "pagamento/index";
    }

    @GetMapping("/pagamentos/novo/{cobrancaId}")
    public String novo(@PathVariable Long cobrancaId, Model model) {
        Cobranca cobranca = pagamentoService.getCobrancaById(cobrancaId);

        if (cobranca == null || cobranca.getStatus() == StatusCobranca.PAGA) {
            return "redirect:/mensalidades?erroPagamento";
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setCobranca(cobranca);
        model.addAttribute("pagamento", pagamento);
        return "pagamento/form";
    }

    @PostMapping("/pagamentos/salvar")
    public String salvar(@ModelAttribute @Valid Pagamento pagamento, BindingResult result, Model model) {

        if (result.hasErrors()) {
            if (pagamento.getCobranca() != null && pagamento.getCobranca().getId() != null) {
                pagamento.setCobranca(pagamentoService.getCobrancaById(pagamento.getCobranca().getId()));
            }
            model.addAttribute("pagamento", pagamento);
            return "pagamento/form";
        }

        try {
            pagamentoService.registrarPagamento(pagamento);
        } catch (IllegalStateException e) {
            return "redirect:/mensalidades?erroPagamento";
        }

        return "redirect:/mensalidades?pago";
    }

}
