package com.academia.gestao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.academia.gestao.service.CobrancaService;

@Controller
public class CobrancaController {

    @Autowired
    private CobrancaService cobrancaService;

    @GetMapping("/mensalidades")
    public String index(Model model) {
        model.addAttribute("cobrancas", cobrancaService.getAllCobrancas());
        return "cobranca/index";
    }

}
