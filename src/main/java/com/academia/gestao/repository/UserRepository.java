package com.academia.gestao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.academia.gestao.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

}
