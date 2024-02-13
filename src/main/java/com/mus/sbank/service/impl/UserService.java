package com.mus.sbank.service.impl;

import com.mus.sbank.dto.BankResponse;
import com.mus.sbank.dto.CreditDebitRequest;
import com.mus.sbank.dto.TransferRequest;
import com.mus.sbank.dto.UserRequest;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);
    BankResponse login(LocaleDate localeDate);

}
