package edu.mcc.codeschool;

import edu.mcc.codeschool.objects.Customer;
import edu.mcc.codeschool.utils.DatabaseUtil;

import java.sql.SQLException;
import java.util.Scanner;

public class Customers {

    public static void createCustomer(Scanner input) {
        Customer customer = new Customer();

        System.out.print("Enter the customer's name: ");
        customer.setName(input.nextLine());

        System.out.print("Enter the customer's DOB (MM/DD/YYYY): ");
        customer.setDOB(input.nextLine());

        System.out.print("Enter the customer's phone number: ");
        customer.setPhoneNumber(input.nextLine());

        System.out.print("Enter the customer's address: ");
        customer.setAddress(input.nextLine());

        System.out.print("Enter the customer's city: ");
        customer.setCity(input.nextLine());

        System.out.print("Enter the customer's state: ");
        String state = input.nextLine();

        while (state.length() != 2) {
            System.out.println("State must be a 2-letter abbreviation.");
            System.out.print("Enter the customer's state: ");
            state = input.nextLine();
        }
        customer.setState(state);

        System.out.print("Enter the customer's zip code: ");
        customer.setZipCode(input.nextInt());
        input.nextLine();

        putCustomerInfo(customer);
    }

    public static void putCustomerInfo(Customer customer) {
        String putCustomerQuery = "INSERT INTO customer (name, dob, phone, street_address, city, state, zip_code) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            DatabaseUtil.executeInsert(putCustomerQuery, rs -> {
                if (rs.next()) {
                    int customerId = rs.getInt(1);
                    System.out.println("\nCustomer created with ID " + customerId);
                }
                return null;
            },  customer.getName(),
                customer.getDOB(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getCity(),
                customer.getState(),
                customer.getZipCode());

        } catch (SQLException e) {
            System.err.println(customer.getName() + " already exists and couldn't be created. Going back to main menu...\n");
        }
    }
}



