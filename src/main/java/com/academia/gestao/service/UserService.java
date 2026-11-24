package com.academia.gestao.service;

import com.academia.gestao.model.User;

public interface UserService {

    Long saveUser(User user);

    void criarAdminInicial();

}
