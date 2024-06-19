package com.example.controller;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import com.example.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void whenTransfer_Success() throws Exception {
        Account sender = new Account();
        sender.setName("Sender");
        sender.setEmail("sender@test.com");
        sender.setBalance(BigDecimal.valueOf(1000));
        sender = accountRepository.save(sender);

        Account recipient = new Account();
        recipient.setName("Recipient");
        recipient.setEmail("recipient@test.com");
        recipient.setBalance(BigDecimal.valueOf(500));
        recipient = accountRepository.save(recipient);

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountId\":" + sender.getId() + ",\"recipientAccountId\":" + recipient.getId() + ",\"amount\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.senderAccount.id", is(sender.getId().intValue())))
                .andExpect(jsonPath("$.recipientAccount.id", is(recipient.getId().intValue())));
    }

    @Test
    void whenTransfer_invalidInput() throws Exception {
        Account recipient = new Account();
        recipient.setName("Recipient");
        recipient.setEmail("recipient@test.com");
        recipient.setBalance(BigDecimal.valueOf(500));
        recipient = accountRepository.save(recipient);

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recipientAccountId\":" + recipient.getId() + ",\"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.senderAccountId").exists())
                .andExpect(jsonPath("$.senderAccountId", is("Send ID is required")));
    }

    @Test
    void transfer_insufficientFunds() throws Exception {
        Account sender = new Account();
        sender.setName("Sender");
        sender.setEmail("sender@example.com");
        sender.setBalance(BigDecimal.valueOf(50));
        sender = accountRepository.save(sender);

        Account recipient = new Account();
        recipient.setName("Recipient");
        recipient.setEmail("recipient@example.com");
        recipient.setBalance(BigDecimal.valueOf(500));
        recipient = accountRepository.save(recipient);

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountId\":" + sender.getId() + ",\"recipientAccountId\":" + recipient.getId() + ",\"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", is("Insufficient funds")));
    }

    @Test
    void transfer_senderNotFound() throws Exception {
        Account recipient = new Account();
        recipient.setName("Recipient");
        recipient.setEmail("recipient@example.com");
        recipient.setBalance(BigDecimal.valueOf(500));
        recipient = accountRepository.save(recipient);

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountId\":999,\"recipientAccountId\":" + recipient.getId() + ",\"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", is("Sender account not found")));
    }

    @Test
    void transfer_recipientNotFound() throws Exception {
        Account sender = new Account();
        sender.setName("Sender");
        sender.setEmail("sender@example.com");
        sender.setBalance(BigDecimal.valueOf(1000));
        sender = accountRepository.save(sender);

        mockMvc.perform(post("/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senderAccountId\":" + sender.getId() + ",\"recipientAccountId\":999,\"amount\":100}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")))
                .andExpect(jsonPath("$.message", is("Recipient account not found")));
    }
}