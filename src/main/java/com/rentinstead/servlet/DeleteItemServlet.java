package com.rentinstead.servlet;

import com.rentinstead.dao.ItemDAO;
import com.rentinstead.model.Item;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/delete-item")
public class DeleteItemServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();

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

        String itemIdText = request.getParameter("itemId");

        if (itemIdText == null || !itemIdText.matches("[0-9]{1,18}")) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item ID."
            );
            return;
        }

        try {
            long itemId = Long.parseLong(itemIdText);
            long userId = ((Number) session.getAttribute("userId")).longValue();

            if (itemId <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid item ID."
                );
                return;
            }

            Item item = itemDAO.findById(itemId);

            if (item == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Item not found."
                );
                return;
            }

            if (item.getOwnerId() != userId) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You can only delete your own item."
                );
                return;
            }

            boolean deleted = itemDAO.deleteById(itemId, userId);

            if (!deleted) {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Item could not be deleted."
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/html/items/browse.html"
            );

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item ID."
            );
        } catch (SQLException e) {
            throw new ServletException("Unable to delete item.", e);
        }
    }
}