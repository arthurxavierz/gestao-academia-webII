package com.academia.gestao.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.academia.gestao.config.AppConfig;
import com.academia.gestao.config.SecurityConfig;
import com.academia.gestao.config.TestConfig;
import com.academia.gestao.model.User;
import com.academia.gestao.service.UserService;

@WebMvcTest(UserController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @AfterEach
    void resetMocks() {
        reset(userService);
    }

    @Test
    @DisplayName("GET /login - Abre a tela de login sem estar logado")
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"));
    }

    @Test
    @DisplayName("GET / - Usuário não logado é mandado para o login")
    void testRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET /register - Abre o cadastro de usuário")
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerUser"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @DisplayName("POST /saveUser - Salva usuário com dados válidos")
    void testSaveUser() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(1L);

        mockMvc.perform(post("/saveUser").with(csrf())
                .param("name", "Maria")
                .param("email", "maria@academia.com")
                .param("password", "123456")
                .param("roles", "Recepcao"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerUser"))
                .andExpect(model().attributeExists("msg"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    @DisplayName("POST /saveUser - Não salva com campos vazios")
    void testSaveUserInvalid() throws Exception {
        mockMvc.perform(post("/saveUser").with(csrf())
                .param("name", "")
                .param("email", "email-invalido")
                .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("user/registerUser"))
                .andExpect(model().attributeHasFieldErrors("user", "name", "email", "password"));

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /accessDenied - Abre a tela de acesso negado para usuário logado")
    void testAccessDeniedPage() throws Exception {
        mockMvc.perform(get("/accessDenied"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/accessDeniedPage"));
    }

}
