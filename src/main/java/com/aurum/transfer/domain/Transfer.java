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
    
    public long getId() {
    	return id;
    }
	public String getIdempotency_key() {
		return idempotency_key;
	}
	public void setIdempotency_key(String idempotency_key) {
		this.idempotency_key = idempotency_key;
	}
	public long getUser_id() {
		return user_id;
	}
	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}
	public long getFrom_account_id() {
		return from_account_id;
	}
	public void setFrom_account_id(long from_account_id) {
		this.from_account_id = from_account_id;
	}
	public long getTo_account_id() {
		return to_account_id;
	}
	public void setTo_account_id(long to_account_id) {
		this.to_account_id = to_account_id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public Date getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(Date updated_at) {
		this.updated_at = updated_at;
	}
	public String getExternal_id() {
		return external_id;
	}
	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}
}
