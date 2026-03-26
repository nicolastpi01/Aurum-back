package com.aurum.transfer.web;

import com.aurum.service.TransferService;
import com.aurum.transfer.dto.TransferRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Void> createTransfer(@RequestHeader("X-Idempotency-Key") String idempotencyKey, @Valid @RequestBody TransferRequest request) {
        transferService.transfer(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
