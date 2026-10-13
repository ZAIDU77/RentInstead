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

@WebServlet("/admin-categories")
public class AdminCategoriesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in first.");
            return;
        }

        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required.");
            return;
        }

        String sql = """
                SELECT
                    COALESCE(NULLIF(TRIM(category), ''), '') AS category_name,
                    COUNT(*) AS item_count
                FROM items
                GROUP BY COALESCE(NULLIF(TRIM(category), ''), '')
                ORDER BY category_name
                """;

        List<Map<String, Object>> categories = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                Map<String, Object> category = new LinkedHashMap<>();
                category.put("name", result.getString("category_name"));
                category.put("itemCount", result.getLong("item_count"));
                categories.add(category);
            }

            request.setAttribute("categories", categories);

            request.getRequestDispatcher(
                    "/WEB-INF/admin-categories.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to retrieve item categories.", e);
        }
    }
}
