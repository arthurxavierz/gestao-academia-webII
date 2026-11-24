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

import com.academia.gestao.model.Modalidade;
import com.academia.gestao.service.ModalidadeService;

import jakarta.validation.Valid;

@Controller
public class ModalidadeController {

    @Autowired
    private ModalidadeService modalidadeService;

    @GetMapping("/modalidades")
    public String index(Model model) {
        model.addAttribute("modalidades", modalidadeService.getAllModalidades());
        return "modalidade/index";
    }

    @GetMapping("/modalidades/novo")
    public String novo(Model model) {
        model.addAttribute("modalidade", new Modalidade());
        return "modalidade/form";
    }

    @PostMapping("/modalidades/salvar")
    public String salvar(@ModelAttribute @Valid Modalidade modalidade, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("modalidade", modalidade);
            return "modalidade/form";
        }

        modalidadeService.saveModalidade(modalidade);
        return "redirect:/modalidades";
    }

    @GetMapping("/modalidades/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Modalidade modalidade = modalidadeService.getModalidadeById(id);

        if (modalidade == null) {
            return "redirect:/modalidades";
        }

        model.addAttribute("modalidade", modalidade);
        return "modalidade/form";
    }

    @PostMapping("/modalidades/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @ModelAttribute @Valid Modalidade modalidade,
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            modalidade.setId(id);
            model.addAttribute("modalidade", modalidade);
            return "modalidade/form";
        }

        modalidadeService.updateModalidade(id, modalidade);
        return "redirect:/modalidades";
    }

    @GetMapping("/modalidades/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        try {
            modalidadeService.deleteModalidade(id);
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return "redirect:/modalidades?erroExclusao";
        }
        return "redirect:/modalidades";
    }

}
