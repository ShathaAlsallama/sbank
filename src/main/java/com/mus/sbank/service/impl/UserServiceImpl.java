package com.mus.sbank.service.impl;

import com.mus.sbank.config.JwtTokenProvider;
import com.mus.sbank.dto.*;
import com.mus.sbank.entity.Role;
import com.mus.sbank.entity.User;
import com.mus.sbank.repository.UserRepository;
import com.mus.sbank.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor

public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PasswordEncoder passwordEncoder;

    private User sourceAccountUser;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        /*  Creating an account - saving anew user in to the db
        * Check if user already has an account  */

        if(userRepository.existsByEmail(userRequest.getEmail())){

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(passwordEncoder.encode(userRequest.getEmail()))
                .password(userRequest.getPassword())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN"))
                .build();

        User savedUser = userRepository.save(newUser);
        //send email alert
        EmailDetails emailDetails =EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has Been Successfully  Created. \n Your Account Details: \n" +
                        "Acccount Name: " + savedUser.getFirstName() + " " +savedUser.getLastName() +" " + savedUser.getOtherName() + "\nAccount Number:  " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " +savedUser.getOtherName())
                        .build())
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );
        EmailDetails loginAlert = EmailDetails.builder()
                .subject("You'r logged on!")
                .recipient(loginDto.getEmail())
                .messageBody("You logged into your account. If you did not initiate this request, please contact your bank")
                .build();
        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();

    }
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //Check if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)

                    .build();

        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                .accountBalance(foundUser.getAccountBalance())
                .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                .accountNumber(request.getAccountNumber())
                .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return  foundUser.getFirstName() +" " + foundUser.getLastName() + " " +foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //Checking  if the account exists
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();

        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        //Save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit + " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {

        //Check if the account exists
        //Check if the amount you intend to withdraw is not more than current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (! isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();


        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance =userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if(availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }


       else{
           userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
           userRepository.save(userToDebit);
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("CREDIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);
           return BankResponse.builder()
                   .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                   .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                   .accountInfo(AccountInfo.builder()
                           .accountNumber(request.getAccountNumber())
                           .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                           .accountBalance(userToDebit.getAccountBalance())
                           .build())

                   .build();
        }

    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        //Get the account to debit → check if it exists
        //Check if the amount I'm debiting is not more than the current balance
        //debit the account
        //get the account to credit
        //credit the account
       // boolean isSourceAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
            if(!isDestinationAccountExist){
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)

                        .build();

            }
            User sourceAccount = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
            if(request.getAmount().compareTo(sourceAccount.getAccountBalance()) < 0){
                return BankResponse.builder()
                        .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();

            }
            sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
            String sourceUsername =sourceAccountUser.getFirstName()+ " " +sourceAccountUser.getLastName()+  " " + sourceAccountUser.getOtherName();
            userRepository.save(sourceAccountUser);
            EmailDetails debitAlert = EmailDetails.builder()

                    .subject("DEBIT ALERT")
                    .recipient(sourceAccountUser.getEmail())
                    .messageBody("The sum of " + request.getAmount() + "has been deducted from your account! Your current balance is " + sourceAccountUser.getAccountBalance())
                    .build();
            emailService.sendEmailAlert(debitAlert);



            User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
            destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
            //String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName();
            userRepository.save(destinationAccountUser);
            EmailDetails creditAlert = EmailDetails.builder()

                .subject("CREDIT ALERT")
                .recipient(sourceAccountUser.getEmail())
                .messageBody("The sum of " + request.getAmount() + "has been sent to your account from "+ sourceUsername +   " Your current balance is " + sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);
        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();



    }

    @Override
    public BankResponse login(LocaleDate localeDate) {
        return null;
    }


    //balance Enquiry,name Enquiry, credit,debit,transfer

}
