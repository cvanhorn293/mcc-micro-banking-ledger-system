package edu.mcc.codeschool;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.mcc.codeschool.objects.Account;
import edu.mcc.codeschool.objects.Transaction;
import edu.mcc.codeschool.utils.DatabaseUtil;
import edu.mcc.codeschool.utils.ErrorHandlingUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Scanner;
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
        String sql = "INSERT INTO transactions (transaction_id, account_id, amount, type, merchant_name, merchant_type, date_time) VALUES (?, (SELECT account_id FROM account WHERE account_number = ?), ?, ?, ?, ?, ?)";
        String dateTime = getDate();

        try {
            DatabaseUtil.executeInsert(sql, rs -> {
                if (rs.next()) {
                    System.out.println("\nTransaction simulated...");
                }
                return null;
            },  transaction.getTransactionID(),
                transaction.getId(),
                transaction.getAmount(),
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
                    "SET balance = balance + ? " +
                    "WHERE account_number = (SELECT account_number FROM account WHERE account_id = (SELECT account_id FROM transactions WHERE transaction_id = ?))";
        } else if (Objects.equals(transaction.getTransactionType(), "CREDIT")) {
            sql = "UPDATE account " +
                    "SET balance = balance - ? " +
                    "WHERE account_number = (SELECT account_number FROM account WHERE account_id = (SELECT account_id FROM transactions WHERE transaction_id = ?))";
        } else {
            System.out.println("Unknown transaction type: " + transaction.getTransactionType());
            return;
        }

        try {
            DatabaseUtil.executeUpdateOrDelete(sql, transaction.getAmount(), transaction.getTransactionID());
        } catch (SQLException e) {
            System.out.println("Error updating balance after transaction: " + e.getMessage());
        }
    }

    public static void depositOrWithdraw(Scanner input) {
        Account account = new Account();

        System.out.println("D - Deposit Funds");
        System.out.println("W - Withdraw Funds");

        System.out.print("\nSelection: ");
        String selection = input.nextLine();

        if (selection.equalsIgnoreCase("D")) {
            depositFunds(account, input);
        } else if (selection.equalsIgnoreCase("W")) {
            withdrawFunds(account, input);
        } else {
            System.out.println("Invalid selection. Please try again.");
            depositOrWithdraw(input);
        }
    }

    private static void depositFunds(Account account, Scanner input) {
        while (ErrorHandlingUtil.getAndCheckAccountNumber(input, account));

        System.out.print("Enter the amount to deposit: ");
        BigDecimal amount = new BigDecimal(input.nextLine());

        BigDecimal existingBalance = getExistingBalance(account);
        BigDecimal newBalance = existingBalance.add(amount);

        updateBalance(account, newBalance);

        System.out.println("\nDeposited $" + amount + " to account number " + account.getAccountNumber());
        System.out.println("Total balance: $" + newBalance.toPlainString());
    }

    private static void withdrawFunds(Account account, Scanner input) {
        while (ErrorHandlingUtil.getAndCheckAccountNumber(input, account));

        System.out.print("Enter the amount to withdraw: ");
        BigDecimal amount = new BigDecimal(input.nextLine());

        BigDecimal existingBalance = getExistingBalance(account);

        while (amount.compareTo(existingBalance) > 0) {
            System.out.println("\nInsufficient funds, current available balance is: $" + existingBalance);
            System.out.print("\nEnter the amount to withdraw: ");
            amount = new BigDecimal(input.nextLine());
        }

        BigDecimal newBalance = existingBalance.subtract(amount);
        updateBalance(account, newBalance);

        System.out.println("\nWithdrew $" + amount + " from account number " + account.getAccountNumber());
        System.out.println("Total balance: $" + newBalance);
        System.out.println("Give that bitch their money foo.");
    }

    public static BigDecimal getExistingBalance(Account account) {
        String sql = "SELECT balance FROM account WHERE account_number = ?";

        try {
            return DatabaseUtil.executeQuery(sql, rs -> {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
                return new BigDecimal("0.0");
            }, account.getAccountNumber());

        } catch (SQLException e) {
            System.out.println("Error retrieving account balance: " + e.getMessage());
            return new BigDecimal("0.0");
        }
    }

    private static void updateBalance(Account account, BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE account_number = ?";

        try {
            DatabaseUtil.executeUpdateOrDelete(sql, newBalance, account.getAccountNumber());
        } catch (SQLException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
        }
    }

    public static String getDate() {
        LocalDate date = LocalDate.now();
        return date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
}
