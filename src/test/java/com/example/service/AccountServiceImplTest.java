package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;
    @InjectMocks
    private AccountServiceImpl accountService;

    private AutoCloseable closeable;
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void whenCreateAccount_returnAccount() {
        Account account = new Account();
        account.setName("TestName");
        account.setEmail("test@test.com");
        account.setBalance(new BigDecimal("100"));

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account createdAccount = accountService.createAccount("TestName", "test@test.com", BigDecimal.valueOf(100.00));

        assertEquals("TestName", createdAccount.getName());
        assertEquals("test@test.com", createdAccount.getEmail());
        assertEquals(BigDecimal.valueOf(100), createdAccount.getBalance());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void whenGetAccountById_returnAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setName("TestName");
        account.setEmail("test@test.com");
        account.setBalance(new BigDecimal("100"));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Optional<Account> foundAccount = accountService.getAccountById(1L);

        assertTrue(foundAccount.isPresent());
        assertEquals("TestName", foundAccount.get().getName());
        assertEquals("test@test.com", foundAccount.get().getEmail());
        assertEquals(BigDecimal.valueOf(100), foundAccount.get().getBalance());

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetAccountById_returnNoAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Account> foundAccount = accountService.getAccountById(1L);

        assertTrue(foundAccount.isEmpty());

        verify(accountRepository, times(1)).findById(1L);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
