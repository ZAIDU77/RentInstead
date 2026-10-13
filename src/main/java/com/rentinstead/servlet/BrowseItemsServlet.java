// ```java
package com.rentinstead.servlet;

import com.rentinstead.dao.ItemDAO;
import com.rentinstead.model.Item;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/browse-items")
public class BrowseItemsServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        try {
            List<Item> items = itemDAO.findAvailableItems();
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);

                if (i > 0) {
                    json.append(",");
                }

                String image = item.getImagePath();

                // Make relative image paths start from the application root.
                if (image != null && !image.trim().isEmpty()
                        && !image.startsWith("http://")
                        && !image.startsWith("https://")
                        && !image.startsWith("/")) {
                    image = request.getContextPath() + "/" + image;
                }

                String location = item.getLocation();

                if (location == null || location.trim().isEmpty()) {
                    location = item.getPincode();
                }

                json.append("{")
                    .append("\"id\":").append(item.getItemId()).append(",")
                    .append("\"title\":").append(jsonString(item.getTitle())).append(",")
                    .append("\"category\":").append(jsonString(item.getCategory())).append(",")
                    .append("\"condition\":").append(jsonString(item.getItemCondition())).append(",")
                    .append("\"description\":").append(jsonString(item.getDescription())).append(",")
                    .append("\"price\":").append(number(item.getRentalPrice())).append(",")
                    .append("\"deposit\":").append(number(item.getSecurityDeposit())).append(",")
                    .append("\"image\":").append(jsonString(image)).append(",")
                    .append("\"location\":").append(jsonString(location)).append(",")
                    .append("\"pincode\":").append(jsonString(item.getPincode())).append(",")
                    .append("\"directions\":").append(jsonString(item.getDirections())).append(",")
                    .append("\"latitude\":").append(decimal(item.getLatitude())).append(",")
                    .append("\"longitude\":").append(decimal(item.getLongitude()))
                    .append("}");
            }

            json.append("]");
            response.getWriter().write(json.toString());

        } catch (SQLException e) {
            getServletContext().log("Unable to load items from database.", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                "{\"error\":\"Unable to load items from the database.\"}"
            );
        }
    }

    private String jsonString(String value) {
        if (value == null) {
            return "null";
        }

        StringBuilder result = new StringBuilder("\"");

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            switch (c) {
                case '"':
                    result.append("\\\"");
                    break;
                case '\\':
                    result.append("\\\\");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    if (c < 32) {
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
            }
        }

        return result.append('"').toString();
    }

    private String number(BigDecimal value) {
        return value == null ? "0" : value.toPlainString();
    }

    private String decimal(Double value) {
        return value == null || !Double.isFinite(value)
                ? "null" : value.toString();
    }
}
