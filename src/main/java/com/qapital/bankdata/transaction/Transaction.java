package com.qapital.bankdata.transaction;


import org.joda.time.LocalDate;

import java.math.BigDecimal;

public class Transaction {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

    public Transaction(Long id, Long userId, BigDecimal amount, String description, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}
