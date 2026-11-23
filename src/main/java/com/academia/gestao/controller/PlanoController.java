package com.academia.gestao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.academia.gestao.model.Plano;
import com.academia.gestao.service.PlanoService;

import jakarta.validation.Valid;

@Controller
public class PlanoController {

    @Autowired
    private PlanoService planoService;

    @GetMapping("/planos")
    public String index(Model model) {
        model.addAttribute("planos", planoService.getAllPlanos());
        return "plano/index";
    }

    @GetMapping("/planos/novo")
    public String novo(Model model) {
        model.addAttribute("plano", new Plano());
        return "plano/form";
    }

    @PostMapping("/planos/salvar")
    public String salvar(@ModelAttribute @Valid Plano plano, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("plano", plano);
            return "plano/form";
        }

        planoService.savePlano(plano);
        return "redirect:/planos";
    }

    @GetMapping("/planos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Plano plano = planoService.getPlanoById(id);

        if (plano == null) {
            return "redirect:/planos";
        }

        model.addAttribute("plano", plano);
        return "plano/form";
    }

    @PostMapping("/planos/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @ModelAttribute @Valid Plano plano, BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            plano.setId(id);
            model.addAttribute("plano", plano);
            return "plano/form";
        }

        planoService.updatePlano(id, plano);
        return "redirect:/planos";
    }

    @GetMapping("/planos/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        try {
            planoService.deletePlano(id);
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return "redirect:/planos?erroExclusao";
        }
        return "redirect:/planos";
    }

}
