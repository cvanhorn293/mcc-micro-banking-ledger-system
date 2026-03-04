package edu.mcc.codeschool;

import edu.mcc.codeschool.objects.Account;
import edu.mcc.codeschool.objects.Customer;
import edu.mcc.codeschool.objects.Transaction;
import edu.mcc.codeschool.utils.DatabaseUtil;
import edu.mcc.codeschool.utils.ErrorHandlingUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class Reports {

    // Generate Statement
    public static void generateStatement(Scanner input) {
        Customer customer = new Customer();

        String filePath = ErrorHandlingUtil.getAndCheckFilePath(input);
        while (ErrorHandlingUtil.getAndCheckCustomerName(input, customer));

        writeStatement(customer, filePath);

        System.out.println("Generated statement for " + customer.getName());
    }

    public static void writeStatement(Customer customer, String filePath) {
        String date = Transactions.getDate();
        String splitName = customer.getName().toLowerCase().replaceAll(" ", "_");

        BigDecimal totalBalance = getTotalBalance(customer);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + "statement_" + splitName + ".txt"))) {
                writer.write("MCC Code School Bank Statement");
                writer.newLine();
                writer.write("\nStatement for " + customer.getName());
                writer.newLine();
                writer.write("Statement Date: " + date);
                writer.newLine();
                writer.write("\nCurrent Total Balance: $" + totalBalance);
                writer.newLine();

                getTransactions(customer, writer);
        } catch (Exception e) {
            System.out.println("Error writing statement: " + e.getMessage());
        }
    }

    private static BigDecimal getTotalBalance(Customer customer) {
        String getTotalBalance =
            "SELECT SUM(a.balance) as total_balance, c.customer_id " +
            "FROM account a " +
            "INNER JOIN customer c ON a.customer_id = c.customer_id " +
            "WHERE c.name = ? " +
            "GROUP BY c.customer_id";

        try {
            return DatabaseUtil.executeQuery(getTotalBalance, rs -> {
                if (rs.next()) {
                    return rs.getBigDecimal("total_balance");
                }
                return new BigDecimal("0.0");
            }, customer.getName());

        } catch (Exception e) {
            System.out.println("Error fetching statement data: " + e.getMessage());
            return new BigDecimal("0.0");
        }
    }

    private static void getTransactions(Customer customer, BufferedWriter writer) {
        String getAccountTransactions =
                "SELECT a.name as account_name, a.balance, " +
                "t.date_time as transaction_date, t.merchant_name, t.type as transaction_type, t.amount " +
                "FROM account a " +
                "INNER JOIN customer c ON a.customer_id = c.customer_id " +
                "LEFT JOIN transactions t ON t.account_id = a.account_id " +
                "WHERE c.name = ? " +
                "ORDER BY a.account_id, t.date_time DESC";

        try {
            DatabaseUtil.executeQuery(getAccountTransactions, rs -> {
                String currentAccount = null;

                while (rs.next()) {
                    String accountName = rs.getString("account_name");
                    BigDecimal balance = rs.getBigDecimal("balance");
                    String transactionDate = rs.getString("transaction_date");
                    String type = rs.getString("transaction_type");

                    try {
                        if (currentAccount == null || !currentAccount.equals(accountName)) {
                            currentAccount = accountName;
                            writer.write("\n" + currentAccount + " - $" + balance + " Balance");
                            writer.newLine();
                            writer.write("Transactions:");
                            writer.newLine();
                        }

                        if (transactionDate != null && type != null) {
                            String typeSign = rs.getString("transaction_type").equalsIgnoreCase("DEBIT") ? "+" : "-";

                            writer.write(transactionDate + " " +  rs.getString("merchant_name") + " " + typeSign + "$" + rs.getBigDecimal("amount"));
                            writer.newLine();
                        } else {
                            writer.write("No transactions");
                            writer.newLine();
                        }

                    } catch (IOException e) {
                        System.out.println("Error writing account balance: " + e.getMessage());
                    }
                }
                return null;
            }, customer.getName());

        } catch (Exception e) {
            System.out.println("Error fetching transaction data: " + e.getMessage());
        }
    }

    public static void generateAccountingReport(Scanner input) {
        String filePath = ErrorHandlingUtil.getAndCheckFilePath(input);

        writeAccountingReport(filePath);

        System.out.println("\nGenerated accounting report");
    }

    private static void writeAccountingReport(String filePath) {
        String date = Transactions.getDate();
        Integer customers = getTotalCustomers();
        Integer accounts = getTotalAccounts();
        BigDecimal totalBalance = getAccountingBalance();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + "accounting_report.txt"))) {
            writer.write("MCC Code School Bank Accounting Report");
            writer.newLine();
            writer.write("\nReport Date: " + date);
            writer.newLine();
            writer.write("\nTotal Customers: " + customers);
            writer.newLine();
            writer.write("Total Accounts: " + accounts);
            writer.newLine();
            writer.write("Total Balance: $" + totalBalance);
            writer.newLine();

            getCustomerAccounts(writer);

        } catch (Exception e) {
            System.out.println("Error writing statement: " + e.getMessage());
        }
    }

    private static BigDecimal getAccountingBalance() {
        String getAccountingBalance = "SELECT SUM(balance) as total_balance FROM account";

        try {
            return DatabaseUtil.executeQuery(getAccountingBalance, rs -> {
                if (rs.next()) {
                    return rs.getBigDecimal("total_balance");
                }
                return new BigDecimal("0.0");
            });

        } catch (Exception e) {
            System.out.println("Error fetching accounting report data: " + e.getMessage());
            return new BigDecimal("0.0");
        }
    }

    private static Integer getTotalCustomers() {
        String sql = "SELECT COUNT(*) as total_customers FROM customer";

        try {
            return DatabaseUtil.executeQuery(sql, rs -> {
                if (rs.next()) {
                    return rs.getInt("total_customers");
                }
                return 0;
            });

        } catch (Exception e) {
            System.out.println("Error fetching total accounts: " + e.getMessage());
            return 0;
        }
    }

    private static Integer getTotalAccounts() {
        String sql = "SELECT COUNT(*) as total_accounts FROM account";

        try {
            return DatabaseUtil.executeQuery(sql, rs -> {
                if (rs.next()) {
                    return rs.getInt("total_accounts");
                }
                return 0;
            });

        } catch (Exception e) {
            System.out.println("Error fetching total accounts: " + e.getMessage());
            return 0;
        }
    }

    private static void getCustomerAccounts(BufferedWriter writer) {
            String getAccountTransactions =
                    "SELECT c.name as customer_name, a.name as account_name, a.balance " +
                    "FROM account a " +
                    "INNER JOIN customer c ON a.customer_id = c.customer_id " +
                    "ORDER BY c.customer_id, a.account_id";

            try {
                DatabaseUtil.executeQuery(getAccountTransactions, rs -> {
                    String currentCustomer = null;

                    while (rs.next()) {
                        String customerName = rs.getString("customer_name");
                        String accountName = rs.getString("account_name");
                        BigDecimal balance = rs.getBigDecimal("balance");

                        try {
                            if (currentCustomer == null || !currentCustomer.equals(customerName)) {
                                currentCustomer = customerName;
                                writer.write("\n" + currentCustomer + ":");
                                writer.newLine();
                            }

                            if (accountName != null) {
                                writer.write("     " + accountName + " - " + "$" + balance);
                                writer.newLine();
                            }
                        } catch (IOException e) {
                            System.out.println("Error writing account balance: " + e.getMessage());
                        }

                    }
                    return null;
                });

            } catch (Exception e) {
                System.out.println("Error fetching transaction data: " + e.getMessage());
            }
    }
}
