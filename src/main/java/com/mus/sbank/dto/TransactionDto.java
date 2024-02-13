package com.mus.sbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
    private String transactionType;
    private String accountNumber;
    private String status;
    private BigDecimal amount;
}
