package com.academia.gestao.model;

public enum Perfil {

    ADMIN("Administrador"),
    RECEPCAO("Recepção"),
    ALUNO("Aluno");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
