package com.academia.gestao.controller;

import static org.hamcrest.Matchers.containsString;
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

import java.time.LocalDate;
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
import com.academia.gestao.model.Aluno;
import com.academia.gestao.service.AlunoService;

@WebMvcTest(AlunoController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class AlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoService alunoService;

    @AfterEach
    void resetMocks() {
        reset(alunoService);
    }

    private Aluno testCreateAluno() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Pereira");
        aluno.setCpf("12345678901");
        aluno.setEmail("joao@email.com");
        aluno.setTelefone("(34) 99999-0000");
        aluno.setDataNascimento(LocalDate.of(2000, 5, 10));
        return aluno;
    }

    @Test
    @DisplayName("GET /alunos - Usuário não logado é mandado para o login")
    void testIndexNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/alunos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /alunos - Recepção vê a lista de alunos")
    void testIndexRecepcao() throws Exception {
        when(alunoService.buscarAlunos(null)).thenReturn(List.of(testCreateAluno()));

        mockMvc.perform(get("/alunos"))
                .andExpect(status().isOk())
                .andExpect(view().name("aluno/index"))
                .andExpect(model().attributeExists("alunos"))
                .andExpect(content().string(containsString("João Pereira")))
                .andExpect(content().string(containsString("10/05/2000")));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /alunos?busca= - Busca o aluno por nome ou CPF")
    void testIndexBusca() throws Exception {
        when(alunoService.buscarAlunos("123")).thenReturn(List.of(testCreateAluno()));

        mockMvc.perform(get("/alunos").param("busca", "123"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("busca", "123"))
                .andExpect(content().string(containsString("12345678901")));

        verify(alunoService).buscarAlunos("123");
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("GET /alunos - Aluno não pode ver a lista de alunos")
    void testIndexAluno() throws Exception {
        mockMvc.perform(get("/alunos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /alunos/novo - Recepção abre o formulário de cadastro")
    void testNovoRecepcao() throws Exception {
        mockMvc.perform(get("/alunos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("aluno/form"))
                .andExpect(model().attributeExists("aluno"));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /alunos/salvar - Falha na validação e retorna para o formulário")
    void testSalvarValidationError() throws Exception {
        mockMvc.perform(post("/alunos/salvar").with(csrf())
                .flashAttr("aluno", new Aluno()))
                .andExpect(status().isOk())
                .andExpect(view().name("aluno/form"))
                .andExpect(model().attributeHasFieldErrors("aluno", "nome", "cpf", "dataNascimento"));

        verify(alunoService, never()).saveAluno(any(Aluno.class));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("POST /alunos/salvar - Aluno válido é salvo")
    void testSalvarValido() throws Exception {
        Aluno aluno = testCreateAluno();
        aluno.setId(null);

        mockMvc.perform(post("/alunos/salvar").with(csrf())
                .flashAttr("aluno", aluno))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/alunos"));

        verify(alunoService).saveAluno(any(Aluno.class));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("POST /alunos/salvar - CPF repetido volta para o formulário com erro")
    void testSalvarCpfDuplicado() throws Exception {
        Aluno aluno = testCreateAluno();
        aluno.setId(null);
        doThrow(new IllegalStateException("Já existe um aluno com este CPF."))
                .when(alunoService).saveAluno(any(Aluno.class));

        mockMvc.perform(post("/alunos/salvar").with(csrf())
                .flashAttr("aluno", aluno))
                .andExpect(status().isOk())
                .andExpect(view().name("aluno/form"))
                .andExpect(model().attributeHasFieldErrors("aluno", "cpf"));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /alunos/atualizar/{id} - Atualiza o aluno")
    void testAtualizar() throws Exception {
        mockMvc.perform(post("/alunos/atualizar/1").with(csrf())
                .flashAttr("aluno", testCreateAluno()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/alunos"));

        verify(alunoService).updateAluno(eq(1L), any(Aluno.class));
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("GET /alunos/excluir/{id} - Aluno não pode excluir")
    void testExcluirAluno() throws Exception {
        mockMvc.perform(get("/alunos/excluir/1"))
                .andExpect(status().isForbidden());

        verify(alunoService, never()).deleteAluno(any());
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /alunos/excluir/{id} - Exclui o aluno")
    void testExcluir() throws Exception {
        mockMvc.perform(get("/alunos/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/alunos"));

        verify(alunoService).deleteAluno(1L);
    }

}
