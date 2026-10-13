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

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        if (name == null || email == null
                || subject == null || message == null
                || name.isBlank() || subject.isBlank()
                || message.isBlank()
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || name.length() > 100
                || email.length() > 254
                || subject.length() > 150
                || message.length() > 5000) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Please enter valid contact details."
            );
            return;
        }

        HttpSession session = request.getSession(false);
        Long userId = null;

        if (session != null && session.getAttribute("userId") instanceof Number) {
            userId = ((Number) session.getAttribute("userId")).longValue();
        }

        String sql = """
                INSERT INTO contact_messages
                    (user_id, name, email, subject, message, status)
                VALUES (?, ?, ?, ?, ?, 'NEW')
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId == null) {
                statement.setNull(1, java.sql.Types.BIGINT);
            } else {
                statement.setLong(1, userId);
            }

            statement.setString(2, name.trim());
            statement.setString(3, email.trim());
            statement.setString(4, subject.trim());
            statement.setString(5, message.trim());

            statement.executeUpdate();

            response.sendRedirect(
                    request.getContextPath()
                    + "/index.html?contact=success"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to save contact message.", e
            );
        }
    }
}