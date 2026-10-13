
package com.rentinstead.dao;

import com.rentinstead.model.Booking;
import com.rentinstead.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public boolean createBooking(Booking booking) throws SQLException {
        if (booking == null
                || booking.getItemId() <= 0
                || booking.getRenterId() <= 0
                || booking.getStartDate() == null
                || booking.getEndDate() == null
                || booking.getEndDate().isBefore(booking.getStartDate())
                || booking.getRentalDays() <= 0
                || booking.getTotalAmount() == null
                || booking.getTotalAmount().signum() < 0) {
            return false;
        }

        String sql = """
            INSERT INTO rentals
                (item_id, renter_id, start_date, end_date,
                 quantity, total_amount, status)
            SELECT ?, ?, ?, ?, 1, ?, 'PENDING'
            FROM items
            WHERE id = ?
              AND owner_id <> ?
              AND available = TRUE
            """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setLong(1, booking.getItemId());
                ps.setLong(2, booking.getRenterId());
                ps.setDate(3, java.sql.Date.valueOf(booking.getStartDate()));
                ps.setDate(4, java.sql.Date.valueOf(booking.getEndDate()));
                ps.setBigDecimal(5, booking.getTotalAmount());
                ps.setLong(6, booking.getItemId());
                ps.setLong(7, booking.getRenterId());

                if (ps.executeUpdate() != 1) {
                    con.rollback();
                    return false;
                }

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        booking.setBookingId(keys.getLong(1));
                    }
                }

                con.commit();
                booking.setBookingStatus("PENDING");
                return true;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public List<Booking> findBookingsByRenter(long renterId)
            throws SQLException {

        List<Booking> bookings = new ArrayList<>();

        String sql = """
            SELECT id, item_id, renter_id, start_date, end_date,
                   quantity, total_amount, status
            FROM rentals
            WHERE renter_id = ?
            ORDER BY id DESC
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, renterId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Booking booking = new Booking();
                    booking.setBookingId(rs.getLong("id"));
                    booking.setItemId(rs.getLong("item_id"));
                    booking.setRenterId(rs.getLong("renter_id"));
                    booking.setStartDate(
                            rs.getDate("start_date").toLocalDate());
                    booking.setEndDate(
                            rs.getDate("end_date").toLocalDate());
                    booking.setRentalDays(
                            (int) java.time.temporal.ChronoUnit.DAYS.between(
                                    booking.getStartDate(),
                                    booking.getEndDate()) + 1);
                    booking.setTotalAmount(rs.getBigDecimal("total_amount"));
                    booking.setBookingStatus(rs.getString("status"));
                    bookings.add(booking);
                }
            }
        }

        return bookings;
    }
}