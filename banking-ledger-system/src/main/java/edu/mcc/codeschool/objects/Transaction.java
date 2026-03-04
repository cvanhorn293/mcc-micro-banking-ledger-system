package edu.mcc.codeschool.objects;

import java.math.BigDecimal;

public class Transaction {
    private String transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String id;
    private String merchantName;
    private String merchantType;

    public String getTransactionID() {
        return transactionId;
    }

    public Transaction setTransactionID(String transactionID) {
        this.transactionId = transactionID;
        return this;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public Transaction setTransactionType(String transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Transaction setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public String getId() {
        return id;
    }

    public Transaction setId(String id) {
        this.id = id;
        return this;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public Transaction setMerchantName(String merchantName) {
        this.merchantName = merchantName;
        return this;
    }

    public String getMerchantType() {
        return merchantType;
    }

    public Transaction setMerchantType(String merchantType) {
        this.merchantType = merchantType;
        return this;
    }
}
