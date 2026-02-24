package com.aurum.accounts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aurum.accounts.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	List<Account> findByUserId(long userId);
}
