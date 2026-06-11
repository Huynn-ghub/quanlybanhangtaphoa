package com.quanlybanhang.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Lớp hỗ trợ kết nối Cơ sở dữ liệu PostgreSQL.
 * Đọc cấu hình từ tài nguyên db.properties hoặc sử dụng cấu hình mặc định.
 */
public class DatabaseHelper {
    private static String url;
    private static String username;
    private static String password;
    private static String adminUsername;
    private static String adminPassword;

    static {
        Properties properties = new Properties();
        try (InputStream is = DatabaseHelper.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                properties.load(is);
                url = properties.getProperty("db.url");
                username = properties.getProperty("db.username");
                password = properties.getProperty("db.password");
                adminUsername = properties.getProperty("admin.username", "admin");
                adminPassword = properties.getProperty("admin.password", "admin123");
            } else {
                System.err.println("Could not find db.properties in resources. Using default connection settings.");
                setDefaultCredentials();
            }
        } catch (IOException e) {
            System.err.println("Error reading db.properties: " + e.getMessage());
            setDefaultCredentials();
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    private static void setDefaultCredentials() {
        url = "jdbc:postgresql://localhost:5432/quanlybanhang";
        username = "postgres";
        password = "postgres";
        adminUsername = "admin";
        adminPassword = "admin123";
    }

    public static String getAdminUsername() {
        return adminUsername;
    }

    public static String getAdminPassword() {
        return adminPassword;
    }

    /**
     * Lấy kết nối tới cơ sở dữ liệu PostgreSQL.
     * @return Connection
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Kiểm tra xem kết nối tới database có thành công hay không.
     * @return true nếu kết nối thành công, ngược lại false.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database Connection Test Failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Đóng các tài nguyên kết nối an toàn.
     */
    public static void close(AutoCloseable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }
}
