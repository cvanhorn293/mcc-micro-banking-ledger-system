package edu.mcc.codeschool;

import java.util.Scanner;

public class Accounts {

    public static void crudAccounts(Scanner input) {
        String selection = accountOptionSelection(input);

        if (selection.equalsIgnoreCase("c")) {
            createAccount(input);
        } else if (selection.equalsIgnoreCase("u")) {
            System.out.println("Updating an account...");
        } else if (selection.equalsIgnoreCase("d")) {
            System.out.println("Deleting an account...");
        } else {
            System.out.println("Invalid selection. Going back to main menu...");
        }

    }

    public static void createAccount(Scanner input) {
        System.out.print("Enter the customer name: ");
        String customerName = input.nextLine();

        System.out.print("Enter the account name: ");
        String accountName = input.nextLine();



        System.out.println("Created account with account number: ");
    }

    public static String accountOptionSelection(Scanner input) {
        System.out.println("What would you like to do?");
        System.out.println("C - Create an account");
        System.out.println("U - Update an account");
        System.out.println("D - Delete an account");

        System.out.print("\nSelection: ");
        return input.nextLine();
    }
}
