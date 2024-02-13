package com.mus.sbank.service.impl;

import com.mus.sbank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailwithAttachment(EmailDetails emailDetails);





}
