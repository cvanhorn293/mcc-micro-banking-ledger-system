package edu.mcc.codeschool.objects;

public class Account {
    private Integer accountID;
    private Integer CustomerID;
    private Double balance;
    private String name;
    private Long accountNumber;
    private String customerName;

    public Integer getAccountID() {
        return accountID;
    }

    public Account setAccountID(Integer accountID) {
        this.accountID = accountID;
        return this;
    }

    public Integer getCustomerID() {
        return CustomerID;
    }

    public Account setCustomerID(Integer customerID) {
        CustomerID = customerID;
        return this;
    }

    public Double getBalance() {
        return balance;
    }

    public Account setBalance(Double balance) {
        this.balance = balance;
        return this;
    }

    public String getName() {
        return name;
    }

    public Account setName(String name) {
        this.name = name;
        return this;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public Account setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Account setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }
}
