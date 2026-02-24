package com.aurum.accounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import com.aurum.transfer.domain.LedgerEntries;
import org.springframework.data.domain.Pageable;


public interface LedgerRepository extends JpaRepository<LedgerEntries, Long> {
	
	Page<LedgerEntries> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

}
