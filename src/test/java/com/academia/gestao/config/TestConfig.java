package com.academia.gestao.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.academia.gestao.service.AlunoService;
import com.academia.gestao.service.InstrutorService;
import com.academia.gestao.service.MatriculaService;
import com.academia.gestao.service.ModalidadeService;
import com.academia.gestao.service.PlanoService;
import com.academia.gestao.service.UserService;

@TestConfiguration
public class TestConfig {

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public PlanoService planoService() {
        return Mockito.mock(PlanoService.class);
    }

    @Bean
    public ModalidadeService modalidadeService() {
        return Mockito.mock(ModalidadeService.class);
    }

    @Bean
    public InstrutorService instrutorService() {
        return Mockito.mock(InstrutorService.class);
    }

    @Bean
    public AlunoService alunoService() {
        return Mockito.mock(AlunoService.class);
    }

    @Bean
    public MatriculaService matriculaService() {
        return Mockito.mock(MatriculaService.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }

}
