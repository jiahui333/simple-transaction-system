package com.example.transactionservice.service;

import com.example.accountservice.entity.Account;
import com.example.transactionservice.entity.Transaction;
import com.example.accountservice.repository.AccountRepository;
import com.example.transactionservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction transfer(Long senderAccountId, Long recipientAccountId, BigDecimal amount) {
        Account senderAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(()-> {
                    logger.error("Sender account not found by Id: {}", senderAccountId);
                    return new IllegalArgumentException("Sender account not found");
                });
        Account recipientAccount = accountRepository.findById(recipientAccountId)
                .orElseThrow(()-> {
                    logger.error("Recipient account not found by Id: {}", recipientAccountId);
                    return new IllegalArgumentException("Recipient account not found");
                });

        if (senderAccount.getBalance().compareTo(amount) <= 0) {
            logger.error("Insufficient funds in sender account with id: {}", senderAccountId);
            throw new IllegalStateException("Insufficient funds");
        }

        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        recipientAccount.setBalance(recipientAccount.getBalance().add(amount));

        accountRepository.save(senderAccount);
        accountRepository.save(recipientAccount);

        Transaction transaction = new Transaction();
        transaction.setSenderAccount(senderAccount);
        transaction.setRecipientAccount(recipientAccount);
        transaction.setAmount(amount);
        transaction.setTimestamp(OffsetDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Transaction successful with transaction id: {}", savedTransaction.getId());
        return savedTransaction;
    }
}
