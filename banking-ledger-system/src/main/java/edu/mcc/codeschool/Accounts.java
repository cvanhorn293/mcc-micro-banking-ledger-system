package edu.mcc.codeschool;

import edu.mcc.codeschool.objects.Account;
import edu.mcc.codeschool.utils.DatabaseUtil;
import edu.mcc.codeschool.utils.ErrorUtil;

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

    public static void createAccount(Scanner input, Account account) {
        System.out.print("Enter the customer name: ");
        account.setCustomerName(input.nextLine());

        // Check if customer exists
        if (!ErrorUtil.checkExisting("customer", "name", account.getCustomerName())) {
            System.out.println("\nCustomer not found. Please try again.\n");
            createAccount(input, account);
        }

        System.out.print("Enter the account name: ");
        account.setName(input.nextLine());

        account.setAccountNumber(generateAccountNumber());
        createAccountQuery(account);
    }

    public static void updateAccount(Scanner input, Account account) {
        System.out.print("Enter the customer's account number: ");
        account.setAccountNumber(input.nextLong());
        input.nextLine();

        if (!ErrorUtil.checkExisting("account", "account_number", account.getAccountNumber())) {
            System.out.println("\nAccount not found. Please try again.\n");
            updateAccount(input, account);
        }

        System.out.print("Enter the new account name: ");
        account.setName(input.nextLine());

        updateAccountQuery(account);
    }

    public static void deleteAccount(Scanner input, Account account) {
        System.out.print("Please enter the customer's account number: ");
        account.setAccountNumber(input.nextLong());
        input.nextLine();

        if (!ErrorUtil.checkExisting("account", "account_number", account.getAccountNumber())) {
            System.out.println("\nAccount not found. Please try again.\n");
            deleteAccount(input, account);
        }

        deleteAccountQuery(account);

    }

    public static void createAccountQuery(Account account) {
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

    public static void updateAccountQuery(Account account) {
        String updateAccountQuery = "UPDATE account SET name = ? WHERE account_number = ?";

        try {
            DatabaseUtil.executeUpdateOrDelete(updateAccountQuery, account.getName(), account.getAccountNumber());
            System.out.println("\nAccount updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating account: " + e.getMessage());
        }
    }

    public static void deleteAccountQuery(Account account) {
        String deleteAccountQuery = "DELETE FROM account WHERE account_number = ?";

        try {
            DatabaseUtil.executeUpdateOrDelete(deleteAccountQuery, account.getAccountNumber());
            System.out.println("\nAccount has been closed.");
        } catch (SQLException e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    public static String accountOptionSelection(Scanner input) {
        System.out.println("What would you like to do?");
        System.out.println("C - Create an account");
        System.out.println("U - Update an account");
        System.out.println("D - Delete an account");

        System.out.print("\nSelection: ");
        return input.nextLine();
    }

    public static Long generateAccountNumber() {
        long min = 1000000000000000L;
        long max = 9999999999999999L;

        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }
}
