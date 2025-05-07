package ru.ifmo.lab6.server.database;

import ru.ifmo.lab6.common.network.User;

import java.sql.*;

public class UserDatabase {
    private final Connection connection;

    public UserDatabase(String url, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(100) PRIMARY KEY,
                password VARCHAR(100) NOT NULL
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public boolean registerUser(User user) {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {
            stmt.setString(1, user.name());
            stmt.setString(2, user.password());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean validateUser(User user) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            stmt.setString(1, user.name());
            stmt.setString(2, user.password());
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}
