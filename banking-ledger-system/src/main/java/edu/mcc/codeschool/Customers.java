package edu.mcc.codeschool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Customers {

    public static void createCustomer(Scanner input) {
        System.out.print("Enter the customer's name: ");
        String name = input.nextLine();

        System.out.print("Enter the customer's DOB (MM/DD/YYYY): ");
        String dob = input.nextLine();

        System.out.print("Enter the customer's phone number: ");
        String phoneNumber = input.nextLine();

        System.out.print("Enter the customer's address: ");
        String address = input.nextLine();

        System.out.print("Enter the customer's city: ");
        String city = input.nextLine();

        System.out.print("Enter the customer's state: ");
        String state = input.nextLine();

        if (state.length() != 2) {
            System.out.println("State must be a 2-letter abbreviation.");
            System.out.print("Enter the customer's state: ");
            state = input.nextLine();
        }

        System.out.print("Enter the customer's zip code: ");
        int zipCode = input.nextInt();
        input.nextLine();

        putCustomerInfo(name, dob, phoneNumber, address, city, state, zipCode);
    }

    public static void putCustomerInfo(String name, String dob, String phoneNumber, String address, String city, String state, int zipCode) {
        String dbURL = "jdbc:sqlite:banking-ledger-system.db";

        try (Connection conn = DriverManager.getConnection(dbURL)) {
            try {
                PreparedStatement prepStatement = conn.prepareStatement(
                        "INSERT INTO customer (name, dob, phone, street_address, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                prepStatement.setString(1, name);
                prepStatement.setString(2, dob);
                prepStatement.setString(3, phoneNumber);
                prepStatement.setString(4, address);
                prepStatement.setString(5, city);
                prepStatement.setString(6, state);
                prepStatement.setInt(7, zipCode);

                prepStatement.executeUpdate();

                ResultSet generatedKeys = prepStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int customerId = generatedKeys.getInt(1);
                    System.out.println("Customer created with ID " + customerId);

                    System.out.println("Customer Information:");
                    System.out.println("ID: " + generatedKeys.getInt(1));
                    System.out.println("Name: " + generatedKeys.getString(2));
                    System.out.println("DOB: " + generatedKeys.getString(3));
                    System.out.println("Phone Number: " + generatedKeys.getString(4));
                    System.out.println("Address: " + generatedKeys.getString(5));
                    System.out.println("City: " + generatedKeys.getString(6));
                    System.out.println("State: " + generatedKeys.getString(7));
                    System.out.println("Zip Code: " + generatedKeys.getInt(8));
                }

            } catch (SQLException e) {
                System.out.println("Error executing query: " + e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

