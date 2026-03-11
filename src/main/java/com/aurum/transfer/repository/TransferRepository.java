package com.aurum.transfer.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aurum.transfer.domain.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
	
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
}
