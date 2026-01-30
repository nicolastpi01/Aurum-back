package com.aurum.transfer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Date;

public class LedgerEntries {
    @Id
    @GeneratedValue
    private long id;
    private long account_id;
    private long transfer_id;

    private String entry_type;
    private double amount;
    private String currency;
    private String description;
    private double balance_after;

    @Column(nullable = false, updatable = false)
    private Date created_at;
    @Column(nullable = false, updatable = false)
    private Date updated_at;
}