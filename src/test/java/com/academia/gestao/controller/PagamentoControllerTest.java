package com.academia.gestao.controller;

import static org.hamcrest.Matchers.containsString;
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
import com.academia.gestao.model.Cobranca;
import com.academia.gestao.model.Matricula;
import com.academia.gestao.model.Pagamento;
import com.academia.gestao.model.Plano;
import com.academia.gestao.model.StatusCobranca;
import com.academia.gestao.service.PagamentoService;

@WebMvcTest(PagamentoController.class)
@Import({ TestConfig.class, SecurityConfig.class, AppConfig.class })
public class PagamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PagamentoService pagamentoService;

    @AfterEach
    void resetMocks() {
        reset(pagamentoService);
    }

    private Cobranca testCreateCobranca(StatusCobranca status) {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("João Pereira");

        Plano plano = new Plano();
        plano.setId(1L);
        plano.setNome("Mensal");

        Matricula matricula = new Matricula();
        matricula.setId(1L);
        matricula.setAluno(aluno);
        matricula.setPlano(plano);

        Cobranca cobranca = new Cobranca();
        cobranca.setId(1L);
        cobranca.setMatricula(matricula);
        cobranca.setParcela(1);
        cobranca.setValor(new BigDecimal("99.90"));
        cobranca.setDataVencimento(LocalDate.of(2026, 11, 3));
        cobranca.setStatus(status);
        return cobranca;
    }

    private Pagamento testCreatePagamento() {
        Pagamento pagamento = new Pagamento();
        pagamento.setCobranca(testCreateCobranca(StatusCobranca.ABERTA));
        pagamento.setFormaPagamento("Pix");
        return pagamento;
    }

    @Test
    @DisplayName("GET /pagamentos - Usuário não logado é mandado para o login")
    void testIndexNotAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /pagamentos - Recepção vê o histórico de pagamentos")
    void testIndexRecepcao() throws Exception {
        Pagamento pagamento = testCreatePagamento();
        pagamento.setId(1L);
        pagamento.setValorPago(new BigDecimal("99.90"));
        pagamento.setDataPagamento(LocalDate.of(2026, 11, 26));
        pagamento.getCobranca().setStatus(StatusCobranca.PAGA);
        when(pagamentoService.getAllPagamentos()).thenReturn(List.of(pagamento));

        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamento/index"))
                .andExpect(content().string(containsString("João Pereira")))
                .andExpect(content().string(containsString("Pix")))
                .andExpect(content().string(containsString("26/11/2026")));
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("GET /pagamentos - Aluno não pode ver os pagamentos")
    void testIndexAluno() throws Exception {
        mockMvc.perform(get("/pagamentos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /pagamentos/novo/{id} - Abre o formulário com os dados da mensalidade")
    void testNovo() throws Exception {
        when(pagamentoService.getCobrancaById(1L)).thenReturn(testCreateCobranca(StatusCobranca.ATRASADA));

        mockMvc.perform(get("/pagamentos/novo/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamento/form"))
                .andExpect(model().attributeExists("pagamento"))
                .andExpect(content().string(containsString("João Pereira")))
                .andExpect(content().string(containsString("R$ 99,90")));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("GET /pagamentos/novo/{id} - Mensalidade já paga volta com aviso")
    void testNovoJaPaga() throws Exception {
        when(pagamentoService.getCobrancaById(1L)).thenReturn(testCreateCobranca(StatusCobranca.PAGA));

        mockMvc.perform(get("/pagamentos/novo/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mensalidades?erroPagamento"));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("POST /pagamentos/salvar - Registra o pagamento e dá baixa na mensalidade")
    void testSalvarValido() throws Exception {
        mockMvc.perform(post("/pagamentos/salvar").with(csrf())
                .flashAttr("pagamento", testCreatePagamento()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mensalidades?pago"));

        verify(pagamentoService).registrarPagamento(any(Pagamento.class));
    }

    @Test
    @WithMockUser(username = "recepcao@academia.com", authorities = { "Recepcao" })
    @DisplayName("POST /pagamentos/salvar - Sem forma de pagamento volta para o formulário")
    void testSalvarValidationError() throws Exception {
        Pagamento pagamento = testCreatePagamento();
        pagamento.setFormaPagamento("");
        when(pagamentoService.getCobrancaById(1L)).thenReturn(testCreateCobranca(StatusCobranca.ABERTA));

        mockMvc.perform(post("/pagamentos/salvar").with(csrf())
                .flashAttr("pagamento", pagamento))
                .andExpect(status().isOk())
                .andExpect(view().name("pagamento/form"))
                .andExpect(model().attributeHasFieldErrors("pagamento", "formaPagamento"));

        verify(pagamentoService, never()).registrarPagamento(any(Pagamento.class));
    }

    @Test
    @WithMockUser(username = "admin@academia.com", authorities = { "Admin" })
    @DisplayName("POST /pagamentos/salvar - Mensalidade já paga não é paga de novo")
    void testSalvarJaPaga() throws Exception {
        doThrow(new IllegalStateException("Esta mensalidade já foi paga."))
                .when(pagamentoService).registrarPagamento(any(Pagamento.class));

        mockMvc.perform(post("/pagamentos/salvar").with(csrf())
                .flashAttr("pagamento", testCreatePagamento()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mensalidades?erroPagamento"));
    }

    @Test
    @WithMockUser(username = "aluno@academia.com", authorities = { "Aluno" })
    @DisplayName("POST /pagamentos/salvar - Aluno não pode registrar pagamento")
    void testSalvarAluno() throws Exception {
        mockMvc.perform(post("/pagamentos/salvar").with(csrf())
                .flashAttr("pagamento", testCreatePagamento()))
                .andExpect(status().isForbidden());

        verify(pagamentoService, never()).registrarPagamento(any(Pagamento.class));
    }

}
