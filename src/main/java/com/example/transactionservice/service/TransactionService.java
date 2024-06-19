package com.example.transactionservice.service;

import com.example.transactionservice.entity.Transaction;

import java.math.BigDecimal;

public interface TransactionService{
    Transaction transfer(Long senderAccountId, Long recipientAccountId, BigDecimal amount);
}
