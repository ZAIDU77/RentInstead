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
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/edit-item")
public class EditItemServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in first."
            );
            return;
        }

        String idText = request.getParameter("itemId");
        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String condition = request.getParameter("itemCondition");
        String description = request.getParameter("description");
        String priceText = request.getParameter("rentalPrice");
        String depositText = request.getParameter("securityDeposit");
        String location = request.getParameter("location");

        if (idText == null || !idText.matches("[0-9]{1,18}")) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item ID."
            );
            return;
        }

        if (title == null || title.isBlank()
                || category == null || category.isBlank()
                || condition == null || condition.isBlank()
                || description == null || description.isBlank()
                || priceText == null || depositText == null
                || location == null || location.isBlank()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Please fill in all required fields."
            );
            return;
        }

        try {
            long itemId = Long.parseLong(idText);
            long ownerId =
                    ((Number) session.getAttribute("userId")).longValue();

            BigDecimal price = new BigDecimal(priceText);
            BigDecimal deposit = new BigDecimal(depositText);

            if (itemId <= 0
                    || price.compareTo(BigDecimal.ZERO) <= 0
                    || deposit.compareTo(BigDecimal.ZERO) < 0
                    || title.length() > 150
                    || category.length() > 80
                    || location.length() > 150
                    || description.length() > 5000) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Please check the item details."
                );
                return;
            }

            Item existingItem = itemDAO.findById(itemId);

            if (existingItem == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Item not found."
                );
                return;
            }

            if (existingItem.getOwnerId() != ownerId) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You can only edit your own item."
                );
                return;
            }

            if (!"AVAILABLE".equals(existingItem.getItemStatus())) {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Only available items can be edited."
                );
                return;
            }

            Item updatedItem = new Item();
            updatedItem.setItemId(itemId);
            updatedItem.setOwnerId(ownerId);
            updatedItem.setTitle(title.trim());
            updatedItem.setCategory(category.trim());
            updatedItem.setItemCondition(condition.trim());
            updatedItem.setDescription(description.trim());
            updatedItem.setRentalPrice(price);
            updatedItem.setSecurityDeposit(deposit);
            updatedItem.setLocation(location.trim());
            updatedItem.setImagePath(existingItem.getImagePath());

            boolean updated = itemDAO.updateItem(updatedItem);

            if (!updated) {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Item could not be updated."
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/item-details?id=" + itemId
            );

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item ID or price."
            );
        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to update item.", e
            );
        }
    }
}