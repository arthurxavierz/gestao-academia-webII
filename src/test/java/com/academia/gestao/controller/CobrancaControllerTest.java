package com.academia.gestao.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.academia.gestao.model.Cobranca;
import com.academia.gestao.model.Matricula;
import com.academia.gestao.model.Plano;
import com.academia.gestao.model.StatusCobranca;
import com.academia.gestao.service.CobrancaService;

@WebMvcTest(CobrancaController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class CobrancaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CobrancaService cobrancaService;

    @AfterEach
    void resetMocks() {
        reset(cobrancaService);
    }

    private Cobranca testCreateCobranca(StatusCobranca status) {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Pereira");

        Plano plano = new Plano();
        plano.setId(1L);
        plano.setNome("Trimestral");

        Matricula matricula = new Matricula();
        matricula.setId(1L);
        matricula.setAluno(aluno);
        matricula.setPlano(plano);

        Cobranca cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setMatricula(matricula);
        cobranca.setParcela(2);
        cobranca.setValor(new BigDecimal("89.96"));
        cobranca.setDataVencimento(LocalDate.of(2026, 12, 20));
        cobranca.setStatus(status);
        return cobranca;
    }

    @Test
    @DisplayName("GET /mensalidades - Usuário não logado é mandado para o login")
    void testIndexNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/mensalidades"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /mensalidades - Recepção vê as mensalidades com valor e vencimento")
    void testIndexRecepcao() throws Exception {
        when(cobrancaService.getAllCobrancas()).thenReturn(List.of(testCreateCobranca(StatusCobranca.ABERTA)));

        mockMvc.perform(get("/mensalidades"))
                .andExpect(status().isOk())
                .andExpect(view().name("cobranca/index"))
                .andExpect(model().attributeExists("cobrancas"))
                .andExpect(content().string(containsString("João Pereira")))
                .andExpect(content().string(containsString("20/12/2026")))
                .andExpect(content().string(containsString("R$ 89,96")))
                .andExpect(content().string(containsString("Aberta")));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /mensalidades - Mostra o status de cada mensalidade")
    void testIndexStatus() throws Exception {
        when(cobrancaService.getAllCobrancas()).thenReturn(List.of(
                testCreateCobranca(StatusCobranca.PAGA), testCreateCobranca(StatusCobranca.ATRASADA)));

        mockMvc.perform(get("/mensalidades"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Paga")))
                .andExpect(content().string(containsString("Atrasada")));
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("GET /mensalidades - Aluno não pode ver as mensalidades de todos")
    void testIndexAluno() throws Exception {
        mockMvc.perform(get("/mensalidades"))
                .andExpect(status().isForbidden());
    }

}
