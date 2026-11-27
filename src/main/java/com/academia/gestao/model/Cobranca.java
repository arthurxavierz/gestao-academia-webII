package com.academia.gestao.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class Cobranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    private Integer parcela;

    private BigDecimal valor;

    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    private StatusCobranca status = StatusCobranca.ABERTA;

}
