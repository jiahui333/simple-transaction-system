package com.example.transactionservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class TransactionRequest {
    @NotNull(message = "Send ID is required")
    private Long senderAccountId;

    @NotNull(message = "Recipient ID is required")
    private Long recipientAccountId;

    @Positive(message = "Transfer amount must be greater than zero")
    private BigDecimal amount;
}
