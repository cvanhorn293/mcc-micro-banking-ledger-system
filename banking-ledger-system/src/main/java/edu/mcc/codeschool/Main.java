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
                // TODO: Implement statement generation
                System.out.println("Generating statement needs to be done...");
            } else if (input.equals("2")) {
                // TODO: Implement accounting report generation
                System.out.println("Generating accounting reports needs to be done...");
            } else if (input.equals("3")) {
                // TODO: Implement transaction simulation
                System.out.println("Simulating a transaction for an account needs to be done...");
            } else if (input.equals("4")) {
                Customers.createCustomer(scanner);
            } else if (input.equals("5")) {
                // TODO: Implement CRUD operations for customer accounts
                System.out.println("CRUD operations for customer accounts need to be done...");
            } else if (input.equals("6")) {
                // TODO: Implement deposit/withdrawal functionality
                System.out.println("Deposit/Withdrawal functionality needs to be done...");
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



