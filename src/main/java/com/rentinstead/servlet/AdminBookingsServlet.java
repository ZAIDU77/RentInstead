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

@WebServlet("/admin-bookings")
public class AdminBookingsServlet extends HttpServlet {

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
                SELECT b.booking_id,
                       b.item_id,
                       b.renter_id,
                       b.booking_status,
                       b.start_date,
                       b.end_date,
                       b.total_amount,
                       b.created_at,
                       i.title AS item_title,
                       u.full_name AS renter_name
                FROM bookings b
                LEFT JOIN items i ON b.item_id = i.item_id
                LEFT JOIN users u ON b.renter_id = u.user_id
                ORDER BY b.created_at DESC
                """;

        List<Map<String, Object>> bookings = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Map<String, Object> booking = new LinkedHashMap<>();

                booking.put("bookingId", result.getLong("booking_id"));
                booking.put("itemId", result.getLong("item_id"));
                booking.put("renterId", result.getLong("renter_id"));
                booking.put("itemTitle", result.getString("item_title"));
                booking.put("renterName", result.getString("renter_name"));
                booking.put(
                        "bookingStatus",
                        result.getString("booking_status")
                );
                booking.put("startDate", result.getDate("start_date"));
                booking.put("endDate", result.getDate("end_date"));
                booking.put("totalAmount", result.getBigDecimal("total_amount"));
                booking.put("createdAt", result.getTimestamp("created_at"));

                bookings.add(booking);
            }

            request.setAttribute("bookings", bookings);

            request.getRequestDispatcher(
                    "/WEB-INF/admin-bookings.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to retrieve bookings.", e
            );
        }
    }
}
