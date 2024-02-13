package com.mus.sbank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
 @AllArgsConstructor
// @Builder
 @NoArgsConstructor

public class CreditDebitRequest {

    private String accountNumber;
    private BigDecimal amount;
}
