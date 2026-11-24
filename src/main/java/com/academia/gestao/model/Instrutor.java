package com.academia.gestao.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class Instrutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é um campo obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve conter pelo menos 3 caracteres")
    private String nome;

    @NotBlank(message = "CREF é um campo obrigatório")
    @Size(max = 30, message = "CREF pode ter no máximo 30 caracteres")
    private String cref;

    @Size(max = 100, message = "Especialidade pode ter no máximo 100 caracteres")
    private String especialidade;

    @ManyToMany
    @JoinTable(name = "instrutor_modalidade",
            joinColumns = @JoinColumn(name = "instrutor_id"),
            inverseJoinColumns = @JoinColumn(name = "modalidade_id"))
    private List<Modalidade> modalidades = new ArrayList<>();

}
