package com.academia.gestao.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Escolha um aluno")
    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @NotNull(message = "Escolha um plano")
    @ManyToOne
    @JoinColumn(name = "plano_id")
    private Plano plano;

    @NotNull(message = "Escolha um instrutor")
    @ManyToOne
    @JoinColumn(name = "instrutor_id")
    private Instrutor instrutor;

    @NotNull(message = "Data de início é um campo obrigatório")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;

    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private StatusMatricula status = StatusMatricula.ATIVA;

    @OneToMany(mappedBy = "matricula")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Cobranca> cobrancas = new ArrayList<>();

}
