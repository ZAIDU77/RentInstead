package com.rentinstead.servlet;

import com.rentinstead.dao.BookingDAO;
import com.rentinstead.model.Booking;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/my-rentals")
public class MyRentalsServlet extends HttpServlet {

    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in to view your rentals."
            );
            return;
        }

        long renterId =
                ((Number) session.getAttribute("userId")).longValue();

        try {
            List<Booking> bookings =
                    bookingDAO.findBookingsByRenter(renterId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store");

            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (Booking booking : bookings) {
                if (!first) {
                    json.append(",");
                }
                first = false;

                json.append("{")
                    .append("\"bookingId\":")
                    .append(booking.getBookingId())
                    .append(",\"itemId\":")
                    .append(booking.getItemId())
                    .append(",\"startDate\":\"")
                    .append(booking.getStartDate())
                    .append("\",\"endDate\":\"")
                    .append(booking.getEndDate())
                    .append("\",\"rentalDays\":")
                    .append(booking.getRentalDays())
                    .append(",\"totalAmount\":")
                    .append(booking.getTotalAmount().toPlainString())
                    .append(",\"status\":\"")
                    .append(escapeJson(booking.getBookingStatus()))
                    .append("\"}");
            }

            json.append("]");
            response.getWriter().write(json.toString());

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to retrieve rental bookings.", e
            );
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}