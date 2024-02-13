package com.mus.sbank.entity;

import com.mus.sbank.service.impl.LocaleDate;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.function.Predicate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;
    private String transactionType;
    private String accountNumber;
    private String status;
    private BigDecimal amount;
    @CreationTimestamp
    private LocaleDate createdAt;
    @UpdateTimestamp
    private LocaleDate modifiedAt;

//    public Predicate<Object> getCreatedAt() {
//    }
}
