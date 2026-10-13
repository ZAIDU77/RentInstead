
package com.rentinstead.dao;

import com.rentinstead.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RentalDAO {

    public boolean isAvailable(
            long itemId,
            LocalDate startDate,
            LocalDate endDate) throws SQLException {

        validateDates(itemId, startDate, endDate);

        String sql = """
            SELECT COUNT(*)
            FROM rentals
            WHERE item_id = ?
              AND status IN ('PENDING', 'APPROVED', 'ACTIVE')
              AND start_date <= ?
              AND end_date >= ?
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, itemId);
            ps.setDate(2, Date.valueOf(endDate));
            ps.setDate(3, Date.valueOf(startDate));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 0;
            }
        }
    }

    public BigDecimal calculateTotal(
            BigDecimal dailyRent,
            LocalDate startDate,
            LocalDate endDate) {

        if (dailyRent == null || dailyRent.signum() < 0) {
            throw new IllegalArgumentException("Invalid daily rent.");
        }

        if (startDate == null || endDate == null
                || endDate.isBefore(startDate)
                || startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid rental dates.");
        }

        // Start date aur end date dono rental days mein included hain.
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        BigDecimal ownerRent =
                dailyRent.multiply(BigDecimal.valueOf(days));

        BigDecimal platformFee =
                new BigDecimal("50.00").multiply(BigDecimal.valueOf(days));

        return ownerRent.add(platformFee);
    }

    public long createRental(
            long itemId,
            long renterId,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal totalAmount) throws SQLException {

        validateDates(itemId, startDate, endDate);

        if (renterId <= 0 || totalAmount == null
                || totalAmount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Invalid renter or rental amount.");
        }

        String sql = """
            INSERT INTO rentals
                (item_id, renter_id, start_date, end_date,
                 status, total_amount)
            SELECT ?, ?, ?, ?, 'PENDING', ?
            WHERE EXISTS (
                SELECT 1
                FROM items
                WHERE id = ?
                  AND available = TRUE
            )
            AND NOT EXISTS (
                SELECT 1
                FROM rentals
                WHERE item_id = ?
                  AND status IN ('PENDING', 'APPROVED', 'ACTIVE')
                  AND start_date <= ?
                  AND end_date >= ?
            )
            """;

        try (Connection con = DBConnection.getConnection()) {
            boolean previousAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            try {
                // Lock item row to reduce simultaneous booking conflicts.
                try (PreparedStatement lock = con.prepareStatement(
                        "SELECT id FROM items WHERE id = ? FOR UPDATE")) {
                    lock.setLong(1, itemId);

                    try (ResultSet rs = lock.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return -1;
                        }
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(sql,
                                     PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setLong(1, itemId);
                    ps.setLong(2, renterId);
                    ps.setDate(3, Date.valueOf(startDate));
                    ps.setDate(4, Date.valueOf(endDate));
                    ps.setBigDecimal(5, totalAmount);
                    ps.setLong(6, itemId);
                    ps.setLong(7, itemId);
                    ps.setDate(8, Date.valueOf(endDate));
                    ps.setDate(9, Date.valueOf(startDate));

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return -1;
                    }

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            long rentalId = keys.getLong(1);
                            con.commit();
                            return rentalId;
                        }
                    }
                }

                con.rollback();
                return -1;

            } catch (SQLException | RuntimeException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(previousAutoCommit);
            }
        }
    }

    private void validateDates(
            long itemId,
            LocalDate startDate,
            LocalDate endDate) {

        if (itemId <= 0 || startDate == null || endDate == null
                || startDate.isBefore(LocalDate.now())
                || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "Please select valid rental dates.");
        }
    }
}
