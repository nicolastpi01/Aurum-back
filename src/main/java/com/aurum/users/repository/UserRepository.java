package com.aurum.users.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.aurum.users.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMail(String mail);
}

