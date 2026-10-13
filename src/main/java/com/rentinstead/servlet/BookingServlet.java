package com.rentinstead.servlet;

import com.rentinstead.dao.BookingDAO;
import com.rentinstead.dao.ItemDAO;
import com.rentinstead.model.Booking;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@WebServlet("/book-item")
public class BookingServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();
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
                    "Please log in before booking an item."
            );
            return;
        }

        try {
            long renterId =
                    ((Number) session.getAttribute("userId")).longValue();

            long itemId = Long.parseLong(request.getParameter("itemId"));

            LocalDate startDate =
                    LocalDate.parse(request.getParameter("startDate"));

            LocalDate endDate =
                    LocalDate.parse(request.getParameter("endDate"));

            if (itemId <= 0
                    || startDate.isBefore(LocalDate.now())
                    || endDate.isBefore(startDate)) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Please select valid booking dates."
                );
                return;
            }

            Item item = itemDAO.findById(itemId);

            if (item == null || !"AVAILABLE".equals(item.getItemStatus())) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "This item is not available."
                );
                return;
            }

            if (item.getOwnerId() == renterId) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "You cannot book your own item."
                );
                return;
            }

            long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

            if (days <= 0 || days > 365) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "The booking duration must be between 1 and 365 days."
                );
                return;
            }

            BigDecimal totalAmount =
                    item.getRentalPrice().multiply(BigDecimal.valueOf(days));

            Booking booking = new Booking(
                    itemId,
                    renterId,
                    startDate,
                    endDate,
                    Math.toIntExact(days),
                    totalAmount
            );

            if (bookingDAO.createBooking(booking)) {
                response.sendRedirect(
                        request.getContextPath() + "/html/rentals/my-rentals.html"
                );
            } else {
                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Booking could not be created."
                );
            }

        } catch (NumberFormatException | java.time.DateTimeException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid item ID or booking date."
            );
        } catch (SQLException e) {
            throw new ServletException(
                    "Database error while creating booking.", e
            );
        }
    }
}