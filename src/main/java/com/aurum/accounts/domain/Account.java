package com.aurum.accounts.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Date;

//import javax.management.relation.Role;

@Entity
public class Account {
    @Id
    @GeneratedValue
    private long id;
    //ForeignKey
    private long user_id;
    private String currency;
    private String status;
    private double balance;

    private Date created_at;
    private Date updated_at;
}
