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
import java.util.List;
import java.util.Locale;

@WebServlet("/api/items/nearby")
public class NearbyItemsServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        try {
            double lat = Double.parseDouble(request.getParameter("lat"));
            double lng = Double.parseDouble(request.getParameter("lng"));

            if (!Double.isFinite(lat) || lat < -90 || lat > 90
                    || !Double.isFinite(lng) || lng < -180 || lng > 180) {
                response.sendError(400, "Invalid coordinates.");
                return;
            }

            String query = request.getParameter("q");
            query = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

            List<Item> items = itemDAO.findNearbyItems(lat, lng);

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (Item item : items) {
                String searchable = (
                    String.valueOf(item.getTitle()) + " " +
                    String.valueOf(item.getCategory()) + " " +
                    String.valueOf(item.getDescription())
                ).toLowerCase(Locale.ROOT);

                if (!query.isEmpty() && !searchable.contains(query)) {
                    continue;
                }

                if (!first) json.append(",");
                first = false;

                json.append("{")
                    .append("\"id\":").append(item.getItemId()).append(",")
                    .append("\"title\":\"").append(escape(item.getTitle())).append("\",")
                    .append("\"category\":\"").append(escape(item.getCategory())).append("\",")
                    .append("\"description\":\"").append(escape(item.getDescription())).append("\",")
                    .append("\"pricePerDay\":").append(
                        item.getRentalPrice() == null ? "0" : item.getRentalPrice().toPlainString()
                    ).append(",")
                    .append("\"imageUrl\":\"").append(escape(item.getImagePath())).append("\",")
                    .append("\"latitude\":").append(item.getLatitude() == null ? "null" : item.getLatitude()).append(",")
                    .append("\"longitude\":").append(item.getLongitude() == null ? "null" : item.getLongitude())
                    .append("}");
            }

            json.append("]");
            response.getWriter().write(json.toString());

        } catch (IllegalArgumentException e) {
            response.sendError(400, "Valid lat and lng parameters are required.");
        } catch (SQLException e) {
            throw new ServletException("Nearby item search failed.", e);
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}


