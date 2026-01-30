package com.aurum.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true, length = 50)
    private String mail;
    @Column(nullable = false, length = 255)
    private String password_hash;
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;
    
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;
    
    @PrePersist
    void onCreate() {
        Date now = new Date();
        created_at = now;
        updated_at = now;
    }
    
    @PreUpdate
    void onUpdate() {
        updated_at = new Date();
    }
    
    public Long getId() { return id; }
    public String getMail() { return mail; }
    public String getPassword_hash() { return password_hash; }
    public Role getRole() { return role; }
    public Status getStatus() { return status; }

    public void setMail(String mail) { this.mail = mail; }
    public void setPassword_hash(String password_hash) { this.password_hash = password_hash; }
    public void setRole(Role role) { this.role = role; }
    public void setStatus(Status status) { this.status = status; }
}