package com.mus.sbank.controller;
import java.io.FileNotFoundException;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.mus.sbank.entity.Transaction;
import com.mus.sbank.service.impl.BankStatement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {
    private BankStatement bankStatement;
    @GetMapping
    public List<Transaction> generateBankStatement(@RequestParam String accountNumber, @RequestParam String startDate, @RequestParam String endDate) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate);

    }
}
