
package com.rentinstead.dao;

import com.rentinstead.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentDAO {

    public boolean paymentExists(long rentalId) throws SQLException {
        if (rentalId <= 0) {
            return false;
        }

        String sql =
                "SELECT payment_id FROM payments WHERE rental_id = ? LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, rentalId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}
