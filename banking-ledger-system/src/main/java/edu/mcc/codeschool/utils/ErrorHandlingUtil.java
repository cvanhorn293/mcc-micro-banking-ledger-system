package edu.mcc.codeschool.utils;

import edu.mcc.codeschool.objects.Account;
import edu.mcc.codeschool.objects.Customer;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class ErrorHandlingUtil {

    private ErrorHandlingUtil() {}

    public static boolean checkExisting(String tableName, String columnName, Object value) {
        String sql = "SELECT COUNT(*) FROM "+ tableName + " WHERE " + columnName + " = ?";

        try {
            Integer count = DatabaseUtil.executeQuery(sql, rs -> {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            },  value);

            return count == null || count <= 0;

        } catch (Exception e) {
            System.out.println("Error checking for existing customer: " + e.getMessage());
            return true;
        }
    }

    public static boolean getAndCheckAccountNumber(Scanner input, Account account) {
        System.out.print("Enter the customer's account number: ");
        account.setAccountNumber(input.nextLong());
        input.nextLine();

        if (checkExisting("account", "account_number", account.getAccountNumber())) {
            System.out.println("\nAccount not found. Please try again.\n");
            return true;
        }
        return false;
    }

    public static boolean getAndCheckCustomerName(Scanner input, Account account) {
        System.out.print("Enter the customer name: ");
        account.setCustomerName(input.nextLine());

        if (checkExisting("customer", "name", account.getCustomerName())) {
            System.out.println("\nCustomer not found. Please try again.\n");
            return true;
        }
        return false;
    }

    public static boolean getAndCheckCustomerName(Scanner input, Customer customer) {
        System.out.print("Enter the customer name: ");
        customer.setName(input.nextLine());

        if (checkExisting("customer", "name", customer.getName())) {
            System.out.println("\nCustomer not found. Please try again.\n");
            return true;
        }
        return false;
    }

    public static String getAndCheckFilePath(Scanner input) {
        System.out.print("Enter the file path to save the report: ");
        String filePath = input.nextLine();

        File directory = new File(filePath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid file path. Please try again.");
            return getAndCheckFilePath(input);
        }

        // Ensure path ends with a separator
        if (!filePath.endsWith(File.separator)) {
            filePath += File.separator;
        }

        return filePath;
    }
}
