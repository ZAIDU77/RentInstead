
package com.rentinstead.servlet;

import com.rentinstead.dao.ItemDAO;
import com.rentinstead.model.Item;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/add-item")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024,
    maxRequestSize = 27 * 1024 * 1024
)
public class AddItemServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                "Please log in before listing an item."
            );
            return;
        }

        String title = trim(request.getParameter("itemTitle"));
        String category = trim(request.getParameter("category"));
        String condition = trim(request.getParameter("condition"));
        String description = trim(request.getParameter("description"));
        String priceText = trim(request.getParameter("price"));
        String depositText = trim(request.getParameter("deposit"));
        String location = trim(request.getParameter("location"));
        String pincode = trim(request.getParameter("pincode"));
        String directions = trim(request.getParameter("directions"));

        if (blank(title) || blank(category) || blank(condition)
                || blank(description) || blank(priceText)
                || blank(location) || blank(pincode)
                || blank(directions)) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Please complete all required fields."
            );
            return;
        }

        if (title.length() < 3 || title.length() > 150
                || category.length() > 80
                || description.length() > 2000
                || location.length() > 150
                || directions.length() > 500
                || !pincode.matches("[0-9]{6}")) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Please check the item details and six-digit PIN code."
            );
            return;
        }

        String normalizedCondition;

        switch (condition.toLowerCase()) {
            case "new":
                normalizedCondition = "NEW";
                break;
            case "like new":
                normalizedCondition = "LIKE_NEW";
                break;
            case "good":
                normalizedCondition = "GOOD";
                break;
            case "fair":
                normalizedCondition = "FAIR";
                break;
            default:
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item condition."
                );
                return;
        }

        BigDecimal price;
        BigDecimal deposit;

        try {
            price = new BigDecimal(priceText);
            deposit = new BigDecimal(
                blank(depositText) ? "0" : depositText
            );

            if (price.signum() <= 0 || deposit.signum() < 0
                    || price.precision() > 10
                    || deposit.precision() > 10
                    || price.scale() > 2
                    || deposit.scale() > 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Enter a valid price and security deposit."
            );
            return;
        }

        Double latitude = parseCoordinate(
            request.getParameter("latitude"), -90, 90
        );
        Double longitude = parseCoordinate(
            request.getParameter("longitude"), -180, 180
        );

        if (latitude == null || longitude == null) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Please select a valid pickup location using GPS or the map."
            );
            return;
        }

        String imagePath = null;
        Part imagePart = null;

        try {
            imagePart = request.getPart("itemImages");

            if (imagePart != null && imagePart.getSize() > 0) {
                String contentType = imagePart.getContentType();

                if (contentType == null ||
                    !(contentType.equals("image/jpeg")
                    || contentType.equals("image/png")
                    || contentType.equals("image/webp"))) {
                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Only JPEG, PNG and WebP photos are allowed."
                    );
                    return;
                }

                String extension;

                if (contentType.equals("image/jpeg")) {
                    extension = ".jpg";
                } else if (contentType.equals("image/png")) {
                    extension = ".png";
                } else {
                    extension = ".webp";
                }

                Path uploadDir = Paths.get(
                    System.getProperty("catalina.base"),
                    "RentInsteadUploads"
                );

                Files.createDirectories(uploadDir);

                String filename = UUID.randomUUID() + extension;
                Path destination = uploadDir.resolve(filename);

                try (java.io.InputStream input =
                         imagePart.getInputStream()) {
                    Files.copy(
                        input,
                        destination,
                        StandardCopyOption.REPLACE_EXISTING
                    );
                }

                imagePath = "item-image/" + filename;
            }

            long ownerId =
                ((Number) session.getAttribute("userId")).longValue();

            Item item = new Item(
                ownerId,
                title,
                category,
                normalizedCondition,
                description,
                price,
                deposit,
                location,
                imagePath
            );

            item.setLatitude(latitude);
            item.setLongitude(longitude);
            item.setPincode(pincode);
            item.setDirections(directions);

            if (itemDAO.addItem(item)) {
                response.sendRedirect(
                    request.getContextPath()
                    + "/html/items/browse.html?saved=1"
                );
            } else {
                response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "The item could not be saved."
                );
            }

        } catch (IllegalStateException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Upload is too large. Each photo must be at most 5 MB."
            );
        } catch (SQLException e) {
            throw new ServletException(
                "Database error while saving the item.", e
            );
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private Double parseCoordinate(
            String value, double min, double max) {

        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            double coordinate = Double.parseDouble(value.trim());

            if (!Double.isFinite(coordinate)
                    || coordinate < min || coordinate > max) {
                return null;
            }

            return coordinate;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}