package com.example.accountservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


import java.math.BigDecimal;

@Getter
public class AccountCreationRequest {
    @NotBlank(message = "Name is required and cannot be empty")
    private String name;

    @NotBlank(message = "Email is required and cannot be empty")
    private String email;

    @Min(value =0, message = "Opening amount must be zero or greater")
    private BigDecimal openingAmount;
}
