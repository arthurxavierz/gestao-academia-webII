package com.academia.gestao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.academia.gestao.model.User;
import com.academia.gestao.service.UserService;

import jakarta.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "user/registerUser";
    }

    @PostMapping("/saveUser")
    public String saveUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/registerUser";
        }
        Long id = userService.saveUser(user);
        model.addAttribute("msg", "Usuário '" + id + "' cadastrado com sucesso!");
        model.addAttribute("user", new User());
        return "user/registerUser";
    }

    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @GetMapping("/accessDenied")
    public String getAccessDeniedPage() {
        return "user/accessDeniedPage";
    }

}
