package com.aurum.transfer.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Transfer {
    @Id
    @GeneratedValue
    private long id;
    //ForeignKey
    private String external_id;
    //ForeignKey
    private String idempotency_key;
    private long user_id;

    //ForeignKey
    private long from_account_id;
    //ForeignKey
    private long to_account_id;

    private double amount;
    private String currency;
    private String status;
    private String description;

    private Date created_at;
    private Date updated_at;
}
