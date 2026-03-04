package edu.mcc.codeschool;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Code School Bank of Awesomeness!");
        System.out.println("-----------------------------------------------");

        while (true) {
            printMenu();
            System.out.print("\nSelection: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("X")) {
                break;
            } else if (input.equals("1")) {
                Reports.generateStatement(scanner);
            } else if (input.equals("2")) {
                Reports.generateAccountingReport(scanner);
            } else if (input.equals("3")) {
                Transactions.simulateTransaction(scanner);
            } else if (input.equals("4")) {
                Customers.createCustomer(scanner);
            } else if (input.equals("5")) {
                Accounts.crudAccounts(scanner);
            } else if (input.equals("6")) {
                Transactions.depositOrWithdraw(scanner);
            }
        }

        System.out.println("Goodbye!");
    }

    public static void printMenu() {
        System.out.println("\nSelect from the following:");
        System.out.println("1. Generate a statement");
        System.out.println("2. Generate Accounting Reports");
        System.out.println("3. Simulate a Transaction for Account");
        System.out.println("4. Create a Customer");
        System.out.println("5. CRUD a Customer Account");
        System.out.println("6. Deposit/Withdrawal Funds");
        System.out.println("X. Exit");
    }
}



