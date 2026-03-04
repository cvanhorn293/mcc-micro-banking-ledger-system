package edu.mcc.codeschool;

import edu.mcc.codeschool.objects.Account;
import edu.mcc.codeschool.utils.DatabaseUtil;
import edu.mcc.codeschool.utils.ErrorHandlingUtil;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

public class Accounts {

    public static void crudAccounts(Scanner input) {
        Account account = new Account();
        String selection = accountOptionSelection(input);

        if (selection.equalsIgnoreCase("c")) {
            createAccount(input, account);
        } else if (selection.equalsIgnoreCase("u")) {
            updateAccount(input, account);
        } else if (selection.equalsIgnoreCase("d")) {
            deleteAccount(input, account);
        } else {
            System.out.println("Invalid selection. Going back to main menu...");
        }
    }

    private static void createAccount(Scanner input, Account account) {
        while (ErrorHandlingUtil.getAndCheckCustomerName(input, account));

        System.out.print("Enter the account name: ");
        account.setName(input.nextLine());

        account.setAccountNumber(generateAccountNumber());
        createAccountQuery(account);
    }

    private static void updateAccount(Scanner input, Account account) {
        while (ErrorHandlingUtil.getAndCheckAccountNumber(input, account));

        System.out.print("Enter the new account name: ");
        account.setName(input.nextLine());

        updateAccountQuery(account);

    }

    private static void deleteAccount(Scanner input, Account account) {
        while (ErrorHandlingUtil.getAndCheckAccountNumber(input, account));
        deleteAccountQuery(account);
    }

    private static void createAccountQuery(Account account) {
        String createAccountQuery = "INSERT INTO account (customer_ID, name, account_number, balance) VALUES ((SELECT customer_id FROM customer WHERE name = ?), ?, ?, 0.00)";

        try {
            DatabaseUtil.executeInsert(createAccountQuery, rs -> {
                if (rs.next()) {
                    System.out.println("\nCreated account with account number: " + account.getAccountNumber());
                }
                return null;
            },  account.getCustomerName(),
                account.getName(),
                account.getAccountNumber());

        } catch (Exception e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private static void updateAccountQuery(Account account) {
        String updateAccountQuery = "UPDATE account SET name = ? WHERE account_number = ?";

        try {
            DatabaseUtil.executeUpdateOrDelete(updateAccountQuery, account.getName(), account.getAccountNumber());
            System.out.println("\nAccount updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating account: " + e.getMessage());
        }
    }

    private static void deleteAccountQuery(Account account) {
        String deleteAccountQuery = "DELETE FROM account WHERE account_number = ?";

        try {
            DatabaseUtil.executeUpdateOrDelete(deleteAccountQuery, account.getAccountNumber());
            System.out.println("\nAccount has been closed.");
        } catch (SQLException e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    private static String accountOptionSelection(Scanner input) {
        System.out.println("\nWhat would you like to do?");
        System.out.println("C - Create an account");
        System.out.println("U - Update an account");
        System.out.println("D - Delete an account");

        System.out.print("\nSelection: ");
        return input.nextLine();
    }

    private static Long generateAccountNumber() {
        long min = 1000000000000000L;
        long max = 9999999999999999L;

        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }
}
