package com.rentinstead.servlet;

import com.rentinstead.dao.ItemDAO;
import com.rentinstead.model.Item;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/item-details")
public class ItemDetailsServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String itemIdText = request.getParameter("id");

        if (itemIdText == null || !itemIdText.matches("[0-9]{1,18}")) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item ID."
            );
            return;
        }

        try {
            long itemId = Long.parseLong(itemIdText);

            if (itemId <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Item ID must be positive."
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

            if (!"AVAILABLE".equals(item.getItemStatus())) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "This item is not currently available."
                );
                return;
            }

            request.setAttribute("item", item);
            request.getRequestDispatcher(
                    "/WEB-INF/item-details.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item ID."
            );
        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to retrieve item details.", e
            );
        }
    }
}