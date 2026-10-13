package com.rentinstead.servlet;

import com.rentinstead.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/admin-export-reports")
public class AdminExportReportsServlet extends HttpServlet {

    private String csvValue(Object value) {
        String text = value == null ? "" : String.valueOf(value);

        // Prevent spreadsheet formula injection.
        if (text.matches("^[\\s]*[=+@].*")
                || text.matches("^[\\s]*-.*")) {
            text = "'" + text;
        }

        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

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

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=\"rentinstead-report.csv\""
        );

        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM users) AS total_users,
                    (SELECT COUNT(*) FROM users
                     WHERE account_status = 'ACTIVE') AS active_users,
                    (SELECT COUNT(*) FROM items) AS total_items,
                    (SELECT COUNT(*) FROM bookings) AS total_bookings,
                    (SELECT COUNT(*) FROM bookings
                     WHERE booking_status = 'PENDING') AS pending_bookings,
                    (SELECT COUNT(*) FROM contact_messages) AS total_messages,
                    (SELECT COALESCE(SUM(total_amount), 0)
                     FROM bookings
                     WHERE booking_status IN
                     ('APPROVED', 'ACTIVE', 'COMPLETED')) AS booking_amount_total
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery();
             PrintWriter writer = response.getWriter()) {

            writer.write('\uFEFF');

            writer.println(
                    "Metric,Value"
            );

            if (result.next()) {
                writer.println(csvValue("Total Users") + ","
                        + csvValue(result.getLong("total_users")));

                writer.println(csvValue("Active Users") + ","
                        + csvValue(result.getLong("active_users")));

                writer.println(csvValue("Total Items") + ","
                        + csvValue(result.getLong("total_items")));

                writer.println(csvValue("Total Bookings") + ","
                        + csvValue(result.getLong("total_bookings")));

                writer.println(csvValue("Pending Bookings") + ","
                        + csvValue(result.getLong("pending_bookings")));

                writer.println(csvValue("Contact Messages") + ","
                        + csvValue(result.getLong("total_messages")));

                writer.println(csvValue("Booking Amount Total") + ","
                        + csvValue(result.getBigDecimal("booking_amount_total")));
            }

            writer.flush();

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to export admin report.", e
            );
        }
    }
}
