package com.rentinstead.servlet;

import com.rentinstead.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

@WebServlet("/admin-update-user")
public class AdminUpdateUserServlet extends HttpServlet {

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "ACTIVE",
            "BLOCKED"
    );

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in first."
            );
            return;
        }

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required."
            );
            return;
        }

        long userId;

        try {
            userId = Long.parseLong(request.getParameter("userId"));
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid user ID."
            );
            return;
        }

        String status = request.getParameter("status");

        if (userId <= 0 || status == null
                || !ALLOWED_STATUSES.contains(status)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid user details."
            );
            return;
        }

        Object currentUserId = session.getAttribute("userId");

        if (currentUserId instanceof Number
                && ((Number) currentUserId).longValue() == userId) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "You cannot change your own account status here."
            );
            return;
        }

        String sql = """
                UPDATE users
                SET account_status = ?
                WHERE user_id = ?
                  AND role <> 'ADMIN'
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, userId);

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "User not found or cannot be modified."
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/admin-users"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to update user status.", e
            );
        }
    }
}
