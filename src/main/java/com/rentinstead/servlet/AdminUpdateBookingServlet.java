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

@WebServlet("/admin-update-booking")
public class AdminUpdateBookingServlet extends HttpServlet {

    private static final Set<String> ALLOWED_STATUSES = Set.of(
            "APPROVED",
            "REJECTED",
            "ACTIVE",
            "COMPLETED",
            "CANCELLED"
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

        String bookingIdValue = request.getParameter("bookingId");
        String status = request.getParameter("status");

        long bookingId;

        try {
            bookingId = Long.parseLong(bookingIdValue);
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid booking ID."
            );
            return;
        }

        if (bookingId <= 0 || status == null
                || !ALLOWED_STATUSES.contains(status)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid booking details."
            );
            return;
        }

        String sql = """
                UPDATE bookings
                SET booking_status = ?
                WHERE booking_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setLong(2, bookingId);

            int updatedRows = statement.executeUpdate();

            if (updatedRows == 0) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Booking not found."
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/admin-bookings"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to update booking status.", e
            );
        }
    }
}
