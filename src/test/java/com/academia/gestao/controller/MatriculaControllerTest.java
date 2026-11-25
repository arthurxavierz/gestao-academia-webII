package com.academia.gestao.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
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

import java.math.BigDecimal;
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
import com.academia.gestao.model.Instrutor;
import com.academia.gestao.model.Matricula;
import com.academia.gestao.model.Plano;
import com.academia.gestao.model.StatusMatricula;
import com.academia.gestao.service.MatriculaService;

@WebMvcTest(MatriculaController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class MatriculaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MatriculaService matriculaService;

    @AfterEach
    void resetMocks() {
        reset(matriculaService);
    }

    private Aluno testCreateAluno() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Pereira");
        aluno.setCpf("12345678901");
        return aluno;
    }

    private Plano testCreatePlano() {
        Plano plano = new Plano();
        plano.setId(1L);
        plano.setNome("Mensal");
        plano.setValor(new BigDecimal("99.90"));
        plano.setDuracaoDias(30);
        return plano;
    }

    private Instrutor testCreateInstrutor() {
        Instrutor instrutor = new Instrutor();
        instrutor.setId(1L);
        instrutor.setNome("Carlos Souza");
        instrutor.setCref("012345-G/MG");
        return instrutor;
    }

    private Matricula testCreateMatricula() {
        Matricula matricula = new Matricula();
        matricula.setId(1L);
        matricula.setAluno(testCreateAluno());
        matricula.setPlano(testCreatePlano());
        matricula.setInstrutor(testCreateInstrutor());
        matricula.setDataInicio(LocalDate.of(2026, 11, 1));
        matricula.setDataFim(LocalDate.of(2026, 12, 1));
        matricula.setStatus(StatusMatricula.ATIVA);
        return matricula;
    }

    @Test
    @DisplayName("GET /matriculas - Usuário não logado é mandado para o login")
    void testIndexNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/matriculas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /matriculas - Recepção vê as matrículas com o vencimento")
    void testIndexRecepcao() throws Exception {
        when(matriculaService.getAllMatriculas()).thenReturn(List.of(testCreateMatricula()));

        mockMvc.perform(get("/matriculas"))
                .andExpect(status().isOk())
                .andExpect(view().name("matricula/index"))
                .andExpect(model().attributeExists("matriculas"))
                .andExpect(content().string(containsString("João Pereira")))
                .andExpect(content().string(containsString("Carlos Souza")))
                .andExpect(content().string(containsString("01/12/2026")))
                .andExpect(content().string(containsString("Ativa")));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /matriculas - Matrícula cancelada não mostra o botão de cancelar")
    void testIndexCancelada() throws Exception {
        Matricula matricula = testCreateMatricula();
        matricula.setStatus(StatusMatricula.CANCELADA);
        when(matriculaService.getAllMatriculas()).thenReturn(List.of(matricula));

        mockMvc.perform(get("/matriculas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cancelada")))
                .andExpect(content().string(not(containsString("/matriculas/cancelar/1"))));
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("GET /matriculas - Aluno não pode ver as matrículas")
    void testIndexAluno() throws Exception {
        mockMvc.perform(get("/matriculas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /matriculas/nova - Abre o formulário com alunos e planos ativos")
    void testNova() throws Exception {
        when(matriculaService.getAllAlunos()).thenReturn(List.of(testCreateAluno()));
        when(matriculaService.getPlanosAtivos()).thenReturn(List.of(testCreatePlano()));
        when(matriculaService.getAllInstrutores()).thenReturn(List.of(testCreateInstrutor()));

        mockMvc.perform(get("/matriculas/nova"))
                .andExpect(status().isOk())
                .andExpect(view().name("matricula/form"))
                .andExpect(model().attributeExists("matricula", "alunos", "planos", "instrutores"))
                .andExpect(content().string(containsString("João Pereira")))
                .andExpect(content().string(containsString("Mensal")))
                .andExpect(content().string(containsString("Carlos Souza")));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("POST /matriculas/salvar - Matrícula válida é salva")
    void testSalvarValido() throws Exception {
        Matricula matricula = testCreateMatricula();
        matricula.setId(null);

        mockMvc.perform(post("/matriculas/salvar").with(csrf())
                .flashAttr("matricula", matricula))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/matriculas"));

        verify(matriculaService).saveMatricula(any(Matricula.class));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("POST /matriculas/salvar - Sem data de início volta para o formulário")
    void testSalvarValidationError() throws Exception {
        Matricula matricula = testCreateMatricula();
        matricula.setDataInicio(null);

        mockMvc.perform(post("/matriculas/salvar").with(csrf())
                .flashAttr("matricula", matricula))
                .andExpect(status().isOk())
                .andExpect(view().name("matricula/form"))
                .andExpect(model().attributeHasFieldErrors("matricula", "dataInicio"));

        verify(matriculaService, never()).saveMatricula(any(Matricula.class));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("POST /matriculas/salvar - Aluno com matrícula ativa mostra a mensagem no formulário")
    void testSalvarMatriculaAtiva() throws Exception {
        doThrow(new IllegalStateException("O aluno João Pereira já possui uma matrícula ativa."))
                .when(matriculaService).saveMatricula(any(Matricula.class));

        mockMvc.perform(post("/matriculas/salvar").with(csrf())
                .flashAttr("matricula", testCreateMatricula()))
                .andExpect(status().isOk())
                .andExpect(view().name("matricula/form"))
                .andExpect(content().string(containsString("já possui uma matrícula ativa")));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /matriculas/cancelar/{id} - Cancela a matrícula")
    void testCancelar() throws Exception {
        mockMvc.perform(get("/matriculas/cancelar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/matriculas"));

        verify(matriculaService).cancelarMatricula(1L);
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /matriculas/cancelar/{id} - Matrícula já cancelada volta com aviso")
    void testCancelarJaCancelada() throws Exception {
        doThrow(new IllegalStateException("Esta matrícula já está cancelada."))
                .when(matriculaService).cancelarMatricula(1L);

        mockMvc.perform(get("/matriculas/cancelar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/matriculas?erroCancelamento"));
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("GET /matriculas/cancelar/{id} - Aluno não pode cancelar matrícula")
    void testCancelarAluno() throws Exception {
        mockMvc.perform(get("/matriculas/cancelar/1"))
                .andExpect(status().isForbidden());

        verify(matriculaService, never()).cancelarMatricula(any());
    }

}
