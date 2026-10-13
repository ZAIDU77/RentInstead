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

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private long count(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            getServletContext().log("Dashboard query failed: " + sql, e);
        }
        return 0;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required.");
            return;
        }

        request.setAttribute("totalUsers",
                count("SELECT COUNT(*) FROM users"));

        request.setAttribute("activeUsers",
                count("SELECT COUNT(*) FROM users WHERE status = 'ACTIVE'"));

        request.setAttribute("totalItems",
                count("SELECT COUNT(*) FROM items"));

        request.setAttribute("totalBookings",
                count("SELECT COUNT(*) FROM bookings"));

        request.setAttribute("pendingBookings",
                count("SELECT COUNT(*) FROM bookings WHERE status = 'PENDING'"));

        request.setAttribute("newMessages",
                count("SELECT COUNT(*) FROM contact_messages"));

        request.getRequestDispatcher(
                "/WEB-INF/admin-dashboard.jsp"
        ).forward(request, response);
    }
}
