package com.aurum.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class User {
    @Id
    @GeneratedValue
    private long id;
    @Column(nullable = false, unique = true, length = 50)
    private String mail;
    @Column(nullable = false, length = 16)
    private String password_hash;

    private Role role;
    private Status status;
    @Column(nullable = false, updatable = false)
    private Date created_at;
    @Column(nullable = false, updatable = false)
    private Date updated_at;
}