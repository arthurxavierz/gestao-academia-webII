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

import java.math.BigDecimal;
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
import com.academia.gestao.model.Plano;
import com.academia.gestao.service.PlanoService;

@WebMvcTest(PlanoController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class PlanoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanoService planoService;

    @AfterEach
    void resetMocks() {
        reset(planoService);
    }

    private Plano testCreatePlano() {
        Plano plano = new Plano();
        plano.setId(1L);
        plano.setNome("Mensal");
        plano.setValor(new BigDecimal("99.90"));
        plano.setDuracaoDias(30);
        plano.setAtivo(true);
        return plano;
    }

    @Test
    @DisplayName("GET /planos - Usuário não logado é mandado para o login")
    void testIndexNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/planos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /planos - Lista os planos para usuário logado")
    void testIndexAuthenticatedUser() throws Exception {
        when(planoService.getAllPlanos()).thenReturn(List.of(testCreatePlano()));

        mockMvc.perform(get("/planos"))
                .andExpect(status().isOk())
                .andExpect(view().name("plano/index"))
                .andExpect(model().attributeExists("planos"))
                .andExpect(content().string(containsString("Planos cadastrados")))
                .andExpect(content().string(containsString("Mensal")));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /planos - Exibe botão de cadastrar para Admin")
    void testCreateButtonAdmin() throws Exception {
        when(planoService.getAllPlanos()).thenReturn(List.of(testCreatePlano()));

        mockMvc.perform(get("/planos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cadastrar Plano")));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /planos - Botão de cadastrar não aparece para quem não é Admin")
    void testCreateButtonNotAdmin() throws Exception {
        when(planoService.getAllPlanos()).thenReturn(List.of(testCreatePlano()));

        mockMvc.perform(get("/planos"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Cadastrar Plano"))));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /planos/novo - Recepção não pode abrir o cadastro de planos")
    void testNovoNotAdmin() throws Exception {
        mockMvc.perform(get("/planos/novo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /planos/novo - Admin abre o formulário de plano")
    void testNovoAdmin() throws Exception {
        mockMvc.perform(get("/planos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("plano/form"))
                .andExpect(model().attributeExists("plano"));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /planos/salvar - Falha na validação e retorna para o formulário")
    void testSalvarValidationError() throws Exception {
        mockMvc.perform(post("/planos/salvar").with(csrf())
                .flashAttr("plano", new Plano()))
                .andExpect(status().isOk())
                .andExpect(view().name("plano/form"))
                .andExpect(model().attributeHasErrors("plano"));

        verify(planoService, never()).savePlano(any(Plano.class));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /planos/salvar - Plano válido é salvo")
    void testSalvarValido() throws Exception {
        Plano plano = testCreatePlano();
        plano.setId(null);

        mockMvc.perform(post("/planos/salvar").with(csrf())
                .flashAttr("plano", plano))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/planos"));

        verify(planoService).savePlano(any(Plano.class));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /planos/editar/{id} - Abre o formulário com o plano")
    void testEditar() throws Exception {
        when(planoService.getPlanoById(1L)).thenReturn(testCreatePlano());

        mockMvc.perform(get("/planos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("plano/form"))
                .andExpect(content().string(containsString("Mensal")));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /planos/atualizar/{id} - Atualiza o plano")
    void testAtualizar() throws Exception {
        mockMvc.perform(post("/planos/atualizar/1").with(csrf())
                .flashAttr("plano", testCreatePlano()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/planos"));

        verify(planoService).updatePlano(eq(1L), any(Plano.class));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("GET /planos/excluir/{id} - Exclui o plano")
    void testExcluir() throws Exception {
        mockMvc.perform(get("/planos/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/planos"));

        verify(planoService).deletePlano(1L);
    }

}
