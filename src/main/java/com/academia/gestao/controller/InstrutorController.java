package com.academia.gestao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.academia.gestao.model.Instrutor;
import com.academia.gestao.service.InstrutorService;

import jakarta.validation.Valid;

@Controller
public class InstrutorController {

    @Autowired
    private InstrutorService instrutorService;

    @GetMapping("/instrutores")
    public String index(Model model) {
        model.addAttribute("instrutores", instrutorService.getAllInstrutores());
        return "instrutor/index";
    }

    @GetMapping("/instrutores/novo")
    public String novo(Model model) {
        model.addAttribute("instrutor", new Instrutor());
        model.addAttribute("modalidades", instrutorService.getAllModalidades());
        return "instrutor/form";
    }

    @PostMapping("/instrutores/salvar")
    public String salvar(@ModelAttribute @Valid Instrutor instrutor, BindingResult result,
            @RequestParam(required = false) List<Long> modalidadeIds, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("instrutor", instrutor);
            model.addAttribute("modalidades", instrutorService.getAllModalidades());
            return "instrutor/form";
        }

        instrutorService.saveInstrutor(instrutor, modalidadeIds);
        return "redirect:/instrutores";
    }

    @GetMapping("/instrutores/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Instrutor instrutor = instrutorService.getInstrutorById(id);

        if (instrutor == null) {
            return "redirect:/instrutores";
        }

        model.addAttribute("instrutor", instrutor);
        model.addAttribute("modalidades", instrutorService.getAllModalidades());
        return "instrutor/form";
    }

    @PostMapping("/instrutores/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @ModelAttribute @Valid Instrutor instrutor, BindingResult result,
            @RequestParam(required = false) List<Long> modalidadeIds, Model model) {

        if (result.hasErrors()) {
            instrutor.setId(id);
            model.addAttribute("instrutor", instrutor);
            model.addAttribute("modalidades", instrutorService.getAllModalidades());
            return "instrutor/form";
        }

        instrutorService.updateInstrutor(id, instrutor, modalidadeIds);
        return "redirect:/instrutores";
    }

    @GetMapping("/instrutores/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        try {
            instrutorService.deleteInstrutor(id);
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return "redirect:/instrutores?erroExclusao";
        }
        return "redirect:/instrutores";
    }

}
