package com.example.service;

import com.example.entity.Account;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountService {
    Account createAccount(String name, String email, BigDecimal openingAmount);
    Optional<Account> getAccountById(Long id);
}
