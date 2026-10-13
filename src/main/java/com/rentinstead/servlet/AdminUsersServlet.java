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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/admin-users")
public class AdminUsersServlet extends HttpServlet {

    @Override
    protected void doGet(
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

        String sql = """
                SELECT user_id, full_name, phone, role,
                       account_status, created_at
                FROM users
                ORDER BY created_at DESC
                """;

        List<Map<String, Object>> users = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Map<String, Object> user = new LinkedHashMap<>();

                user.put("userId", result.getLong("user_id"));
                user.put("fullName", result.getString("full_name"));
                user.put("phone", result.getString("phone"));
                user.put("role", result.getString("role"));
                user.put("accountStatus", result.getString("account_status"));
                user.put("createdAt", result.getTimestamp("created_at"));

                users.add(user);
            }

            request.setAttribute("users", users);

            request.getRequestDispatcher(
                    "/WEB-INF/admin-users.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to retrieve users.", e
            );
        }
    }
}