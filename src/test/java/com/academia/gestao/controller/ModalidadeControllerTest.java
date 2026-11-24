package com.academia.gestao.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

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
import com.academia.gestao.model.Modalidade;
import com.academia.gestao.service.ModalidadeService;

@WebMvcTest(ModalidadeController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class ModalidadeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModalidadeService modalidadeService;

    @AfterEach
    void resetMocks() {
        reset(modalidadeService);
    }

    private Modalidade testCreateModalidade() {
        Modalidade modalidade = new Modalidade();
        modalidade.setId(1L);
        modalidade.setNome("Musculação");
        modalidade.setDescricao("Treino com pesos e aparelhos");
        return modalidade;
    }

    @Test
    @DisplayName("GET /modalidades - Usuário não logado é mandado para o login")
    void testIndexNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/modalidades"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /modalidades - Lista as modalidades sem o botão de cadastrar para Recepção")
    void testIndexRecepcao() throws Exception {
        when(modalidadeService.getAllModalidades()).thenReturn(List.of(testCreateModalidade()));

        mockMvc.perform(get("/modalidades"))
                .andExpect(status().isOk())
                .andExpect(view().name("modalidade/index"))
                .andExpect(model().attributeExists("modalidades"))
                .andExpect(content().string(containsString("Musculação")))
                .andExpect(content().string(not(containsString("Cadastrar Modalidade"))));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /modalidades - Exibe botão de cadastrar para Admin")
    void testIndexAdmin() throws Exception {
        when(modalidadeService.getAllModalidades()).thenReturn(List.of(testCreateModalidade()));

        mockMvc.perform(get("/modalidades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar Modalidade")));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /modalidades/novo - Recepção não pode cadastrar modalidade")
    void testNovoNotAdmin() throws Exception {
        mockMvc.perform(get("/modalidades/novo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /modalidades/salvar - Falha na validação e retorna para o formulário")
    void testSalvarValidationError() throws Exception {
        mockMvc.perform(post("/modalidades/salvar").with(csrf())
                .flashAttr("modalidade", new Modalidade()))
                .andExpect(status().isOk())
                .andExpect(view().name("modalidade/form"))
                .andExpect(model().attributeHasErrors("modalidade"));

        verify(modalidadeService, never()).saveModalidade(any(Modalidade.class));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /modalidades/salvar - Modalidade válida é salva")
    void testSalvarValido() throws Exception {
        Modalidade modalidade = testCreateModalidade();
        modalidade.setId(null);

        mockMvc.perform(post("/modalidades/salvar").with(csrf())
                .flashAttr("modalidade", modalidade))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/modalidades"));

        verify(modalidadeService).saveModalidade(any(Modalidade.class));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /modalidades/atualizar/{id} - Atualiza a modalidade")
    void testAtualizar() throws Exception {
        mockMvc.perform(post("/modalidades/atualizar/1").with(csrf())
                .flashAttr("modalidade", testCreateModalidade()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/modalidades"));

        verify(modalidadeService).updateModalidade(eq(1L), any(Modalidade.class));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /modalidades/excluir/{id} - Modalidade em plano não é excluída e mostra aviso")
    void testExcluirModalidadeEmPlano() throws Exception {
        doThrow(new IllegalStateException("Modalidade em plano")).when(modalidadeService).deleteModalidade(1L);

        mockMvc.perform(get("/modalidades/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/modalidades?erroExclusao"));
    }

}
