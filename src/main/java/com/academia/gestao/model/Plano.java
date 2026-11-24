package com.academia.gestao.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é um campo obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve conter pelo menos 3 caracteres")
    private String nome;

    @NotNull(message = "Informe o valor do plano")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Informe a duração em dias")
    @Min(value = 1, message = "A duração deve ser de pelo menos 1 dia")
    private Integer duracaoDias;

    private boolean ativo = true;

    @ManyToMany
    @JoinTable(name = "plano_modalidade",
            joinColumns = @JoinColumn(name = "plano_id"),
            inverseJoinColumns = @JoinColumn(name = "modalidade_id"))
    private List<Modalidade> modalidades = new ArrayList<>();

}
