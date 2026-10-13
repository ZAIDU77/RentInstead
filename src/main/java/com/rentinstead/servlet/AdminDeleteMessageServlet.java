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

@WebServlet("/admin-delete-message")
public class AdminDeleteMessageServlet extends HttpServlet {

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

        long messageId;

        try {
            messageId = Long.parseLong(
                    request.getParameter("messageId")
            );
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid message ID."
            );
            return;
        }

        if (messageId <= 0) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid message ID."
            );
            return;
        }

        String sql = """
                DELETE FROM contact_messages
                WHERE message_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, messageId);

            int deletedRows = statement.executeUpdate();

            if (deletedRows == 0) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Message not found."
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/admin-messages"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to delete contact message.", e
            );
        }
    }
}
