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

@WebServlet("/admin-category-items")
public class AdminCategoryItemsServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Check login session
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in first."
            );
            return;
        }

        // Only administrators can access this page
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required."
            );
            return;
        }

        // Validate category parameter
        String category = request.getParameter("category");

        if (category == null || category.trim().isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Category is required."
            );
            return;
        }

        category = category.trim();

        if (category.length() > 100) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Category name cannot exceed 100 characters."
            );
            return;
        }

        String sql = """
                SELECT item_id, title, category, price_per_day, item_status
                FROM items
                WHERE category = ?
                ORDER BY item_id DESC
                """;

        List<Map<String, Object>> items = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // PreparedStatement protects against SQL injection
            statement.setString(1, category);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    Map<String, Object> item = new LinkedHashMap<>();

                    item.put("itemId", result.getInt("item_id"));
                    item.put("title", result.getString("title"));
                    item.put("category", result.getString("category"));
                    item.put(
                            "pricePerDay",
                            result.getBigDecimal("price_per_day")
                    );
                    item.put("status", result.getString("item_status"));

                    items.add(item);
                }
            }

            request.setAttribute("selectedCategory", category);
            request.setAttribute("categoryItems", items);

            request.getRequestDispatcher(
                    "/WEB-INF/admin-category-items.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to retrieve category items.", e
            );
        }
    }
}
