package com.mus.sbank.service.impl;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mus.sbank.dto.EmailDetails;
import com.mus.sbank.entity.Transaction;
import com.mus.sbank.repository.TransactionRepository;
import com.mus.sbank.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;

import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Component
@AllArgsConstructor
@Slf4j

public class BankStatement {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    private static final String FILE = "C:\\Users\\Admin\\Documents\\MyStatement.pdf";
    public String endDate;


    /*
    retrieve list of transaction within a data range given an account number
    generate a pdf file of transactions
    send the file via email
     */

    /*
    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException {
        LocaleDate start = LocaleDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocaleDate end = LocaleDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().<Object>isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();
        User user = userRepository.existsByAccountNumber(accountNumber);

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("setting size of document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();
*/
public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate){
    Locale start = LocaleDate.parse(startDate,DateTimeFormatter.ISO_DATE);
    Locale end = LocaleDate.parse(endDate,DateTimeFormatter.ISO_DATE);
       List<Transaction> transactionList =transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber) )
               .filter(transaction -> transaction.getCreatedAt().isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();






        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Java App"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);
        PdfPCell bankAddress = new PdfPCell(new Phrase("72,Some Address, Lagos Nigeria"));
        bankAddress.setBorder(0);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        String startDate;
        PdfPCell customerInfo =new PdfPCell(new PdfPCell("Start Date: " + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " +endDate));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name:" + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Customer Address " + user.getAddress()));
        address.setBorder(0);


        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date =new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(new Phrase(transaction.getStatus())));
        });
        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);
        document.close();

        EmailDetails emailDetails =EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("KINDLY find your requested account statement attached!")
                .attachment(FILE)
                .build();
        emailService.sendEmailwithAttachment(emailDetails);

        return transactionList;

    }

}
