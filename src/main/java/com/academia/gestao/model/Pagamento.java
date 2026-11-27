package com.academia.gestao.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Escolha a mensalidade")
    @OneToOne
    @JoinColumn(name = "cobranca_id", unique = true)
    private Cobranca cobranca;

    private BigDecimal valorPago;

    private LocalDate dataPagamento;

    @NotBlank(message = "Escolha a forma de pagamento")
    private String formaPagamento;

}
