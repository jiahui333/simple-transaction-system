package com.example.accountservice.service;

import com.example.accountservice.entity.Account;
import com.example.accountservice.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account createAccount(String name, String email, BigDecimal openingAmount) {
        Account account = new Account();
        account.setName(name);
        account.setEmail(email);
        account.setBalance(openingAmount);

        logger.info("Creating a new account: name {}, email {}, and opening amount {}.", account.getName(), account.getEmail(), account.getBalance());
        return accountRepository.save(account);
    }

    @Override
    public Optional<Account> getAccountById(Long id) {
        logger.info("Fetching account by id: {}", id);
        return accountRepository.findById(id);
    }
}
