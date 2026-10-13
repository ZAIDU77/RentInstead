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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin-messages")
public class AdminMessagesServlet extends HttpServlet {

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
                SELECT message_id, user_id, name, email,
                       subject, message, created_at
                FROM contact_messages
                ORDER BY created_at DESC
                """;

        List<Map<String, Object>> messages = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Map<String, Object> message = new LinkedHashMap<>();

                message.put("messageId", result.getLong("message_id"));
                message.put("userId", result.getObject("user_id"));
                message.put("name", result.getString("name"));
                message.put("email", result.getString("email"));
                message.put("subject", result.getString("subject"));
                message.put("message", result.getString("message"));
                message.put("createdAt", result.getTimestamp("created_at"));

                messages.add(message);
            }

            request.setAttribute("messages", messages);

            request.getRequestDispatcher(
                    "/WEB-INF/admin-messages.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to retrieve contact messages.", e
            );
        }
    }
}
