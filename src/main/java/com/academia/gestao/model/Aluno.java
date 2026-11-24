package com.academia.gestao.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é um campo obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve conter pelo menos 3 caracteres")
    private String nome;

    @NotBlank(message = "CPF é um campo obrigatório")
    @Size(min = 11, max = 14, message = "CPF deve ter 11 números")
    @Column(unique = true)
    private String cpf;

    @Email(message = "Informe um e-mail válido")
    private String email;

    @Size(max = 20, message = "Telefone pode ter no máximo 20 caracteres")
    private String telefone;

    @NotNull(message = "Data de nascimento é um campo obrigatório")
    @Past(message = "Data de nascimento deve ser no passado")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

}
