package com.academia.gestao.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import com.academia.gestao.model.Instrutor;
import com.academia.gestao.model.Modalidade;
import com.academia.gestao.service.InstrutorService;

@WebMvcTest(InstrutorController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class InstrutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InstrutorService instrutorService;

    @AfterEach
    void resetMocks() {
        reset(instrutorService);
    }

    private Modalidade testCreateModalidade() {
        Modalidade modalidade = new Modalidade();
        modalidade.setId(1L);
        modalidade.setNome("Musculação");
        return modalidade;
    }

    private Instrutor testCreateInstrutor() {
        Instrutor instrutor = new Instrutor();
        instrutor.setId(1L);
        instrutor.setNome("Carlos Souza");
        instrutor.setCref("012345-G/MG");
        instrutor.setEspecialidade("Hipertrofia");
        instrutor.setModalidades(List.of(testCreateModalidade()));
        return instrutor;
    }

    @Test
    @DisplayName("GET /instrutores - Usuário não logado é mandado para o login")
    void testIndexNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/instrutores"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /instrutores - Lista os instrutores com as modalidades, sem botão de cadastrar para Recepção")
    void testIndexRecepcao() throws Exception {
        when(instrutorService.getAllInstrutores()).thenReturn(List.of(testCreateInstrutor()));

        mockMvc.perform(get("/instrutores"))
                .andExpect(status().isOk())
                .andExpect(view().name("instrutor/index"))
                .andExpect(model().attributeExists("instrutores"))
                .andExpect(content().string(containsString("Carlos Souza")))
                .andExpect(content().string(containsString("Musculação")))
                .andExpect(content().string(not(containsString("Cadastrar Instrutor"))));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /instrutores/novo - Admin abre o formulário com as modalidades")
    void testNovoAdmin() throws Exception {
        when(instrutorService.getAllModalidades()).thenReturn(List.of(testCreateModalidade()));

        mockMvc.perform(get("/instrutores/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("instrutor/form"))
                .andExpect(model().attributeExists("instrutor", "modalidades"))
                .andExpect(content().string(containsString("Musculação")));
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("GET /instrutores/novo - Aluno não pode cadastrar instrutor")
    void testNovoAluno() throws Exception {
        mockMvc.perform(get("/instrutores/novo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /instrutores/salvar - Falha na validação e retorna para o formulário")
    void testSalvarValidationError() throws Exception {
        mockMvc.perform(post("/instrutores/salvar").with(csrf())
                .flashAttr("instrutor", new Instrutor()))
                .andExpect(status().isOk())
                .andExpect(view().name("instrutor/form"))
                .andExpect(model().attributeHasFieldErrors("instrutor", "nome", "cref"));

        verify(instrutorService, never()).saveInstrutor(any(Instrutor.class), any());
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /instrutores/salvar - Instrutor válido é salvo com as modalidades marcadas")
    void testSalvarValido() throws Exception {
        Instrutor instrutor = testCreateInstrutor();
        instrutor.setId(null);

        mockMvc.perform(post("/instrutores/salvar").with(csrf())
                .flashAttr("instrutor", instrutor)
                .param("modalidadeIds", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/instrutores"));

        verify(instrutorService).saveInstrutor(any(Instrutor.class), eq(List.of(1L)));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /instrutores/atualizar/{id} - Atualiza o instrutor")
    void testAtualizar() throws Exception {
        mockMvc.perform(post("/instrutores/atualizar/1").with(csrf())
                .flashAttr("instrutor", testCreateInstrutor()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/instrutores"));

        verify(instrutorService).updateInstrutor(eq(1L), any(Instrutor.class), any());
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /instrutores/excluir/{id} - Exclui o instrutor")
    void testExcluir() throws Exception {
        mockMvc.perform(get("/instrutores/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/instrutores"));

        verify(instrutorService).deleteInstrutor(1L);
    }

}
