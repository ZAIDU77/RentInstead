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

@WebServlet("/admin-update-category")
public class AdminUpdateCategoryServlet extends HttpServlet {

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

        String oldCategory = request.getParameter("oldCategory");
        String newCategory = request.getParameter("newCategory");

        if (oldCategory == null || newCategory == null
                || oldCategory.trim().isEmpty()
                || newCategory.trim().isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Both category names are required."
            );
            return;
        }

        oldCategory = oldCategory.trim();
        newCategory = newCategory.trim();

        if (oldCategory.length() > 100 || newCategory.length() > 100) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Category names cannot exceed 100 characters."
            );
            return;
        }

        if (oldCategory.equalsIgnoreCase(newCategory)) {
            response.sendRedirect(
                    request.getContextPath() + "/admin-categories"
            );
            return;
        }

        String sql = "UPDATE items SET category = ? WHERE category = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newCategory);
            statement.setString(2, oldCategory);

            statement.executeUpdate();

            response.sendRedirect(
                    request.getContextPath() + "/admin-categories"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to update the item category.", e
            );
        }
    }
}
