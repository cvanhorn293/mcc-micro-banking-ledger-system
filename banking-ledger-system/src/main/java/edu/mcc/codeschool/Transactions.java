package edu.mcc.codeschool;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.mcc.codeschool.objects.Account;
import edu.mcc.codeschool.objects.Transaction;
import edu.mcc.codeschool.utils.DatabaseUtil;
import edu.mcc.codeschool.utils.ErrorHandlingUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;

import com.google.gson.Gson;

public class Transactions {

    public static void simulateTransaction(Scanner input) {
        Account account = new Account();
        Transaction transaction = new Transaction();

        while (ErrorHandlingUtil.getAndCheckAccountNumber(input, account));

        System.out.println("Fetching data...");

        getTransactionAPI(account, transaction);
        transactionSimQuery(transaction);
        setBalanceAfterTransaction(transaction);

        System.out.println("\nTransaction simulated...");

    }

    private static void getTransactionAPI(Account account, Transaction transaction) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request =
                HttpRequest.newBuilder(
                URI.create("http://18.207.116.129:8080/transaction/" + account.getAccountNumber()))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonObject getCustomerID = jsonObject.getAsJsonObject("customer");
            JsonObject getRecipientInfo = jsonObject.getAsJsonObject("recipient");

            Transaction transactions = new Gson().fromJson(response.body(), Transaction.class);
            Transaction customerID = new Gson().fromJson(getCustomerID, Transaction.class);
            Transaction recipientInfo = new Gson().fromJson(getRecipientInfo, Transaction.class);

            transaction.setTransactionID(transactions.getTransactionID())
                    .setTransactionType(transactions.getTransactionType())
                    .setAmount(transactions.getAmount())
                    .setId(customerID.getId())
                    .setMerchantName(recipientInfo.getMerchantName())
                    .setMerchantType(recipientInfo.getMerchantType());

        } catch (IOException | InterruptedException e) {
            System.out.println("Error simulating transaction: " + e.getMessage());
        }
    }

    private static void transactionSimQuery(Transaction transaction) {
        String sql = "INSERT INTO transactions (transaction_id, account_id, amount, type, merchant_name, merchant_type, date_time) VALUES (?, (SELECT account_id FROM account WHERE account_number = ?), ROUND(?, 2), ?, ?, ?, ?)";
        String dateTime = getDate();

        try {
            BigDecimal roundedAmount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);

            DatabaseUtil.executeInsert(sql, rs -> {
                rs.next();
                return null;
            },  transaction.getTransactionID(),
                transaction.getId(),
                roundedAmount,
                transaction.getTransactionType(),
                transaction.getMerchantName(),
                transaction.getMerchantType(),
                dateTime);

        } catch (SQLException e) {
            System.out.println("Error recording transaction: " + e.getMessage());
        }
    }

    private static void setBalanceAfterTransaction(Transaction transaction) {
        String sql;

        if (Objects.equals(transaction.getTransactionType(), "DEBIT")) {
            sql = "UPDATE account " +
                    "SET balance = ROUND(balance + ?, 2) " +
                    "WHERE account_number = (SELECT account_number FROM account WHERE account_id = (SELECT account_id FROM transactions WHERE transaction_id = ?))";
        } else if (Objects.equals(transaction.getTransactionType(), "CREDIT")) {
            sql = "UPDATE account " +
                    "SET balance = ROUND(balance - ?, 2) " +
                    "WHERE account_number = (SELECT account_number FROM account WHERE account_id = (SELECT account_id FROM transactions WHERE transaction_id = ?))";
        } else {
            System.out.println("Unknown transaction type: " + transaction.getTransactionType());
            return;
        }

        try {
            BigDecimal roundedAmount = transaction.getAmount().setScale(2, RoundingMode.HALF_UP);
            DatabaseUtil.executeUpdateOrDelete(sql, roundedAmount, transaction.getTransactionID());
        } catch (SQLException e) {
            System.out.println("Error updating balance after transaction: " + e.getMessage());
        }
    }

    public static void depositOrWithdraw(Scanner input) {
        Account account = new Account();
        Transaction transaction = new Transaction();

        System.out.println("D - Deposit Funds");
        System.out.println("W - Withdraw Funds");

        System.out.print("\nSelection: ");
        String selection = input.nextLine();

        if (selection.equalsIgnoreCase("D")) {
            depositFunds(account, transaction, input);
        } else if (selection.equalsIgnoreCase("W")) {
            withdrawFunds(account, transaction, input);
        } else {
            System.out.println("Invalid selection. Please try again.");
            depositOrWithdraw(input);
        }
    }

    private static void depositFunds(Account account, Transaction transaction, Scanner input) {
        while (ErrorHandlingUtil.getAndCheckAccountNumber(input, account));

        System.out.print("Enter the amount to deposit: ");

        BigDecimal amount = new BigDecimal(input.nextLine());
        BigDecimal existingBalance = getExistingBalance(account);
        BigDecimal newBalance = existingBalance.add(amount);

        transaction
            .setTransactionType("DEBIT")
            .setMerchantName("Deposit")
            .setMerchantType("Deposit");
        setTransactionData(account, transaction, amount, newBalance, account.getAccountNumber());

        System.out.println("\nDeposited $" + amount + " to account number " + account.getAccountNumber());
        System.out.println("Total balance: $" + newBalance.toPlainString());
    }

    private static void withdrawFunds(Account account, Transaction transaction, Scanner input) {
        while (ErrorHandlingUtil.getAndCheckAccountNumber(input, account));

        BigDecimal existingBalance = getExistingBalance(account);

        if (existingBalance.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("\nAccount has a balance of $0.00. Unable to withdraw any money. Returning to main menu...");
            return;
        }

        System.out.print("Enter the amount to withdraw: ");

        BigDecimal amount = new BigDecimal(input.nextLine());

        while (amount.compareTo(existingBalance) > 0) {
            System.out.println("\nInsufficient funds, current available balance is: $" + existingBalance);
            System.out.print("\nEnter the amount to withdraw: ");
            amount = new BigDecimal(input.nextLine());
        }

        BigDecimal newBalance = existingBalance.subtract(amount);

        transaction
            .setTransactionType("CREDIT")
            .setMerchantName("Withdrawal")
            .setMerchantType("Withdrawal");
        setTransactionData(account, transaction, amount, newBalance, account.getAccountNumber());

        System.out.println("\nWithdrew $" + amount + " from account number " + account.getAccountNumber());
        System.out.println("Total balance: $" + newBalance);
        System.out.println("Give that bitch their money foo.");
    }

    private static void setTransactionData(Account account, Transaction transaction, BigDecimal amount, BigDecimal newBalance, Long accountNum) {
        String transactionID = createTransactionID();

        transaction.setTransactionID(transactionID);
        transaction.setId(accountNum);
        transaction.setAmount(amount);

        transactionSimQuery(transaction);
        updateBalance(account, newBalance);
    }

    public static BigDecimal getExistingBalance(Account account) {
        String sql = "SELECT ROUND(balance, 2) as balance FROM account WHERE account_number = ?";

        try {
            return DatabaseUtil.executeQuery(sql, rs -> {
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    return balance != null ? balance.setScale(2, RoundingMode.HALF_UP) : new BigDecimal("0.00");
                }
                return new BigDecimal("0.00");
            }, account.getAccountNumber());

        } catch (SQLException e) {
            System.out.println("Error retrieving account balance: " + e.getMessage());
            return new BigDecimal("0.00");
        }
    }

    private static void updateBalance(Account account, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ROUND(?, 2) WHERE account_number = ?";

        try {
            BigDecimal roundedBalance = newBalance.setScale(2, RoundingMode.HALF_UP);
            DatabaseUtil.executeUpdateOrDelete(sql, roundedBalance, account.getAccountNumber());
        } catch (SQLException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
        }
    }

    public static String getDate() {
        LocalDate date = LocalDate.now();
        return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    private static String createTransactionID() {
        return UUID.randomUUID().toString();
    }
}
