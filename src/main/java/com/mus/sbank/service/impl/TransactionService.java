package com.mus.sbank.service.impl;

import com.mus.sbank.dto.TransactionDto;
import com.mus.sbank.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
