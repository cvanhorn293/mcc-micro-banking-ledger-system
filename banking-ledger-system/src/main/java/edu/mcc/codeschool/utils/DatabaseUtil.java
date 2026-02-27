package edu.mcc.codeschool.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String DATABASE_URL = "jdbc:sqlite:banking-ledger-system.db";

    private DatabaseUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    public static <T> T executeQuery(String query, ResultSetHandler<T> handler, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement prepStatement = conn.prepareStatement(query)) {

            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    prepStatement.setNull(i + 1, java.sql.Types.NULL);
                } else {
                    prepStatement.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet resultSet = prepStatement.executeQuery()) {
                return handler.handle(resultSet);
            }
        }
    }

    public static <T> T executeInsert(String sql, ResultSetHandler<T> handler, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement prepStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                prepStatement.setObject(i + 1, params[i]);
            }

            prepStatement.executeUpdate();

            try (ResultSet resultSet = prepStatement.getGeneratedKeys()) {
                return handler.handle(resultSet);
            }
        }
    }

    public static int executeUpdateOrDelete(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
            PreparedStatement prepStatement = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                prepStatement.setObject(i + 1, params[i]);
            }

            return prepStatement.executeUpdate();
        }
    }

    @FunctionalInterface
    public interface ResultSetHandler<T> {
        T handle(ResultSet resultSet) throws SQLException;
    }

}


