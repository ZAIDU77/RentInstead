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
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/admin-reports")
public class AdminReportsServlet extends HttpServlet {

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

        Map<String, Object> reports = new LinkedHashMap<>();

        String[] queries = {
            "SELECT COUNT(*) FROM users",
            "SELECT COUNT(*) FROM users WHERE account_status = 'ACTIVE'",
            "SELECT COUNT(*) FROM items",
            "SELECT COUNT(*) FROM bookings",
            "SELECT COUNT(*) FROM bookings WHERE booking_status = 'PENDING'",
            "SELECT COUNT(*) FROM contact_messages"
        };

        String[] keys = {
            "totalUsers",
            "activeUsers",
            "totalItems",
            "totalBookings",
            "pendingBookings",
            "totalMessages"
        };

        try (Connection connection = DBConnection.getConnection()) {

            for (int i = 0; i < queries.length; i++) {
                try (PreparedStatement statement =
                             connection.prepareStatement(queries[i]);
                     ResultSet result = statement.executeQuery()) {

                    if (result.next()) {
                        reports.put(keys[i], result.getLong(1));
                    } else {
                        reports.put(keys[i], 0L);
                    }
                }
            }

            String revenueSql = """
                    SELECT COALESCE(SUM(total_amount), 0)
                    FROM bookings
                    WHERE booking_status IN ('APPROVED', 'ACTIVE', 'COMPLETED')
                    """;

            try (PreparedStatement statement =
                         connection.prepareStatement(revenueSql);
                 ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    reports.put("totalRevenue", result.getBigDecimal(1));
                }
            }

            request.setAttribute("reports", reports);

            request.getRequestDispatcher(
                    "/WEB-INF/admin-reports.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to generate admin reports.", e
            );
        }
    }
}
