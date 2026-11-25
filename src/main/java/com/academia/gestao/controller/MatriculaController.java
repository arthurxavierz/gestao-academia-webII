package com.academia.gestao.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.academia.gestao.model.Matricula;
import com.academia.gestao.service.MatriculaService;

import jakarta.validation.Valid;

@Controller
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @GetMapping("/matriculas")
    public String index(Model model) {
        model.addAttribute("matriculas", matriculaService.getAllMatriculas());
        return "matricula/index";
    }

    @GetMapping("/matriculas/nova")
    public String nova(Model model) {
        Matricula matricula = new Matricula();
        matricula.setDataInicio(LocalDate.now());

        model.addAttribute("matricula", matricula);
        model.addAttribute("alunos", matriculaService.getAllAlunos());
        model.addAttribute("planos", matriculaService.getPlanosAtivos());
        return "matricula/form";
    }

    @PostMapping("/matriculas/salvar")
    public String salvar(@ModelAttribute @Valid Matricula matricula, BindingResult result, Model model) {

        if (!result.hasErrors()) {
            try {
                matriculaService.saveMatricula(matricula);
                return "redirect:/matriculas";
            } catch (IllegalStateException e) {
                result.addError(new ObjectError("matricula", e.getMessage()));
            }
        }

        model.addAttribute("alunos", matriculaService.getAllAlunos());
        model.addAttribute("planos", matriculaService.getPlanosAtivos());
        return "matricula/form";
    }

    @GetMapping("/matriculas/cancelar/{id}")
    public String cancelar(@PathVariable Long id) {
        try {
            matriculaService.cancelarMatricula(id);
        } catch (IllegalStateException e) {
            return "redirect:/matriculas?erroCancelamento";
        }
        return "redirect:/matriculas";
    }

}
