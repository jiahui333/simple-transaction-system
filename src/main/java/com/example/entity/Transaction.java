package com.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_account_id", referencedColumnName = "id", nullable = false)
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = "recipient_account_id", referencedColumnName = "id", nullable = false)
    private Account recipientAccount;

    private BigDecimal amount;

    private OffsetDateTime timestamp;
}
