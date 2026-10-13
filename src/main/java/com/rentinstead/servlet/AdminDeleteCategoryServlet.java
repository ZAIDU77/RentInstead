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

@WebServlet("/admin-delete-category")
public class AdminDeleteCategoryServlet extends HttpServlet {

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

        String category = request.getParameter("category");

        if (category == null || category.trim().isEmpty()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Category is required."
            );
            return;
        }

        /*
         * Categories are stored directly in the items table.
         * Deleting a category therefore clears its category value
         * instead of deleting the items themselves.
         */

        String sql = """
                UPDATE items
                SET category = NULL
                WHERE category = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, category.trim());
            statement.executeUpdate();

            response.sendRedirect(
                    request.getContextPath() + "/admin-categories"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to remove the item category.", e
            );
        }
    }
}
