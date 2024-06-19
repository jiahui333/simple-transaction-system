package com.example.transaction.service;

import com.example.accountservice.entity.Account;
import com.example.transactionservice.entity.Transaction;
import com.example.accountservice.repository.AccountRepository;
import com.example.transactionservice.repository.TransactionRepository;
import com.example.transactionservice.service.TransactionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenTransfer_createTransaction() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setBalance(new BigDecimal("1000"));

        Account recipient = new Account();
        recipient.setId(2L);
        recipient.setBalance(new BigDecimal("600"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(recipient));

        Transaction transaction = new Transaction();
        transaction.setSenderAccount(sender);
        transaction.setRecipientAccount(recipient);
        transaction.setAmount(new BigDecimal("100"));
        transaction.setTimestamp(OffsetDateTime.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction createdTransaction = transactionService.transfer(1L, 2L, new BigDecimal("100"));

        assertNotNull(createdTransaction);
        assertEquals(new BigDecimal("900"), sender.getBalance());
        assertEquals(new BigDecimal("700"), recipient.getBalance());
        assertEquals(sender, createdTransaction.getSenderAccount());
        assertEquals(recipient, createdTransaction.getRecipientAccount());
        assertEquals(new BigDecimal("100"), createdTransaction.getAmount());

        verify(accountRepository, times(1)).save(sender);
        verify(accountRepository, times(1)).save(recipient);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void whenTransfer_InsufficientFunds() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setBalance(new BigDecimal("60"));

        Account recipient = new Account();
        recipient.setId(2L);
        recipient.setBalance(new BigDecimal("600"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(recipient));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                transactionService.transfer(1L, 2L, new BigDecimal("100")));

        assertEquals("Insufficient funds", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void whenTransfer_SenderAccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(1L, 2L, new BigDecimal("100")));

        assertEquals("Sender account not found", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void whenTransfer_RecipientAccountNotFound() {
        Account sender = new Account();
        sender.setId(1L);
        sender.setBalance(new BigDecimal("1000"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.transfer(1L, 2L, new BigDecimal("100")));

        assertEquals("Recipient account not found", exception.getMessage());

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }
}
