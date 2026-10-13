
package com.rentinstead.dao;

import com.rentinstead.model.User;
import com.rentinstead.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public boolean registerUser(User user) throws SQLException {
        String sql = """
                INSERT INTO users (name, phone, password, role)
                VALUES (?, ?, ?, 'STUDENT')
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getPasswordHash());

            if (ps.executeUpdate() != 1) {
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getLong(1));
                } else {
                    throw new SQLException("User ID not generated.");
                }
            }

            user.setRole("STUDENT");
            user.setAccountStatus("ACTIVE");
            return true;
        }
    }

    public User findByPhone(String phone) throws SQLException {
        String sql = """
                SELECT id, name, phone, password, role
                FROM users
                WHERE phone = ?
                LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    public User findById(long userId) throws SQLException {
        String sql = """
                SELECT id, name, phone, password, role
                FROM users
                WHERE id = ?
                LIMIT 1
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapUser(rs) : null;
            }
        }
    }

    public boolean updateProfile(User user) throws SQLException {
        String sql = """
                UPDATE users
                SET name = ?, phone = ?
                WHERE id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setLong(3, user.getUserId());

            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(long userId, String passwordHash)
            throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setLong(2, userId);

            return ps.executeUpdate() == 1;
        }
    }

    public boolean deactivateAccount(long userId) throws SQLException {
        // Current users table has no account_status column.
        return false;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setUserId(rs.getLong("id"));
        user.setFullName(rs.getString("name"));
        user.setPhone(rs.getString("phone"));
        user.setPasswordHash(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setAccountStatus("ACTIVE");

        return user;
    }
}