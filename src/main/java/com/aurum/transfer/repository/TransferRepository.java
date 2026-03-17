package com.aurum.transfer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.aurum.transfer.domain.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
	
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
}
