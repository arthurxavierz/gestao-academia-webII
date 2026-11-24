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
import org.springframework.web.bind.annotation.RequestParam;

import com.academia.gestao.model.Aluno;
import com.academia.gestao.service.AlunoService;

import jakarta.validation.Valid;

@Controller
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping("/alunos")
    public String index(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("alunos", alunoService.buscarAlunos(busca));
        model.addAttribute("busca", busca);
        return "aluno/index";
    }

    @GetMapping("/alunos/novo")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/form";
    }

    @PostMapping("/alunos/salvar")
    public String salvar(@ModelAttribute @Valid Aluno aluno, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("aluno", aluno);
            return "aluno/form";
        }

        try {
            alunoService.saveAluno(aluno);
        } catch (IllegalStateException e) {
            result.rejectValue("cpf", "cpf.duplicado", e.getMessage());
            model.addAttribute("aluno", aluno);
            return "aluno/form";
        }

        return "redirect:/alunos";
    }

    @GetMapping("/alunos/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Aluno aluno = alunoService.getAlunoById(id);

        if (aluno == null) {
            return "redirect:/alunos";
        }

        model.addAttribute("aluno", aluno);
        return "aluno/form";
    }

    @PostMapping("/alunos/atualizar/{id}")
    public String atualizar(@PathVariable Long id, @ModelAttribute @Valid Aluno aluno,
            BindingResult result, Model model) {

        aluno.setId(id);

        if (result.hasErrors()) {
            model.addAttribute("aluno", aluno);
            return "aluno/form";
        }

        try {
            alunoService.updateAluno(id, aluno);
        } catch (IllegalStateException e) {
            result.rejectValue("cpf", "cpf.duplicado", e.getMessage());
            model.addAttribute("aluno", aluno);
            return "aluno/form";
        }

        return "redirect:/alunos";
    }

    @GetMapping("/alunos/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        try {
            alunoService.deleteAluno(id);
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return "redirect:/alunos?erroExclusao";
        }
        return "redirect:/alunos";
    }

}
