package edu.mcc.codeschool.utils;

public class ErrorUtil {

    private ErrorUtil() {}

    public static boolean checkExisting(String tableName, String columnName, Object value) {
        String sql = "SELECT COUNT(*) FROM "+ tableName + " WHERE " + columnName + " = ?";

        try {
            Integer count = DatabaseUtil.executeQuery(sql, rs -> {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            },  value);

            return count != null && count > 0;

        } catch (Exception e) {
            System.out.println("Error checking for existing customer: " + e.getMessage());
            return false;
        }
    }
}
