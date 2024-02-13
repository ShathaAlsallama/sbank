package com.mus.sbank.service.impl;

import com.mus.sbank.dto.TransactionDto;
import com.mus.sbank.entity.Transaction;
import com.mus.sbank.repository.TransactionRepository;
import org.eclipse.angus.mail.imap.SortTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component

//import javax.sound.midi.Soundbank;

public class TransactionImpl implements TransactionService {

    @Autowired
     TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("SUCCESS")
                .build();
        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully");

    }
}
