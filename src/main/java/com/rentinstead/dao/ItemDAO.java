// ```java
package com.rentinstead.dao;

import com.rentinstead.model.Item;
import com.rentinstead.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    private static final double SEARCH_RADIUS_KM = 20.0;
    private static final BigDecimal PLATFORM_FEE =
            new BigDecimal("50.00");

    // Add new item
    public boolean addItem(Item item) throws SQLException {

        String sql = """
            INSERT INTO items
            (owner_id, title, description, price_per_day,
             image_url, available, category_id,
             latitude, longitude, pincode, directions)
            VALUES (?, ?, ?, ?, ?, TRUE, ?, ?, ?, ?, ?)
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, item.getOwnerId());
            ps.setString(2, item.getTitle());
            ps.setString(3, item.getDescription());
            ps.setBigDecimal(4, item.getRentalPrice());
            ps.setString(5, item.getImagePath());

            Integer categoryId =
                    findCategoryId(con, item.getCategory());

            if (categoryId == null) {
                throw new SQLException(
                        "Category not found: " + item.getCategory());
            }

            ps.setInt(6, categoryId);
            setCoordinate(ps, 7, item.getLatitude());
            setCoordinate(ps, 8, item.getLongitude());
            ps.setString(9, item.getPincode());
            ps.setString(10, item.getDirections());

            if (ps.executeUpdate() != 1) {
                return false;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setItemId(keys.getLong(1));
                }
            }

            item.setPlatformFee(PLATFORM_FEE);
            item.setItemStatus("AVAILABLE");

            return true;
        }
    }

    // Find all available items
    public List<Item> findAvailableItems() throws SQLException {

        String sql = """
            SELECT i.*, c.name AS category_name
            FROM items i
            LEFT JOIN categories c ON c.id = i.category_id
            WHERE i.available = TRUE
            ORDER BY i.id DESC
            """;

        List<Item> items = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapItem(rs));
            }
        }

        return items;
    }

    // Find available items within 20 km
    public List<Item> findNearbyItems(
            double customerLatitude,
            double customerLongitude) throws SQLException {

        if (!Double.isFinite(customerLatitude)
                || !Double.isFinite(customerLongitude)
                || customerLatitude < -90
                || customerLatitude > 90
                || customerLongitude < -180
                || customerLongitude > 180) {

            throw new IllegalArgumentException(
                    "Invalid customer coordinates.");
        }

        String sql = """
            SELECT i.*, c.name AS category_name,
                6371 * ACOS(
                    LEAST(1.0, GREATEST(-1.0,
                        COS(RADIANS(?))
                        * COS(RADIANS(i.latitude))
                        * COS(RADIANS(i.longitude) - RADIANS(?))
                        + SIN(RADIANS(?))
                        * SIN(RADIANS(i.latitude))
                    ))
                ) AS distance_km
            FROM items i
            LEFT JOIN categories c ON c.id = i.category_id
            WHERE i.available = TRUE
              AND i.latitude IS NOT NULL
              AND i.longitude IS NOT NULL
            HAVING distance_km <= ?
            ORDER BY distance_km ASC
            """;

        List<Item> items = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, customerLatitude);
            ps.setDouble(2, customerLongitude);
            ps.setDouble(3, customerLatitude);
            ps.setDouble(4, SEARCH_RADIUS_KM);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItem(rs));
                }
            }
        }

        return items;
    }

    // Find item by ID
    public Item findById(long itemId) throws SQLException {

        String sql = """
            SELECT i.*, c.name AS category_name
            FROM items i
            LEFT JOIN categories c ON c.id = i.category_id
            WHERE i.id = ?
            LIMIT 1
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs);
                }
            }
        }

        return null;
    }

    // Update item
    public boolean updateItem(Item item) throws SQLException {

        String sql = """
            UPDATE items
            SET title = ?,
                description = ?,
                price_per_day = ?,
                image_url = ?,
                category_id = ?,
                latitude = ?,
                longitude = ?,
                pincode = ?,
                directions = ?
            WHERE id = ?
              AND owner_id = ?
              AND available = TRUE
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            Integer categoryId =
                    findCategoryId(con, item.getCategory());

            if (categoryId == null) {
                throw new SQLException("Category not found.");
            }

            ps.setString(1, item.getTitle());
            ps.setString(2, item.getDescription());
            ps.setBigDecimal(3, item.getRentalPrice());
            ps.setString(4, item.getImagePath());
            ps.setInt(5, categoryId);
            setCoordinate(ps, 6, item.getLatitude());
            setCoordinate(ps, 7, item.getLongitude());
            ps.setString(8, item.getPincode());
            ps.setString(9, item.getDirections());
            ps.setLong(10, item.getItemId());
            ps.setLong(11, item.getOwnerId());

            return ps.executeUpdate() == 1;
        }
    }

    // Delete item only if it has no rental history
    public boolean deleteById(
            long itemId, long ownerId) throws SQLException {

        String sql = """
            DELETE FROM items
            WHERE id = ?
              AND owner_id = ?
              AND NOT EXISTS (
                  SELECT 1
                  FROM rentals r
                  WHERE r.item_id = items.id
              )
            """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, itemId);
            ps.setLong(2, ownerId);

            return ps.executeUpdate() == 1;
        }
    }

    // Find category ID using its name
    private Integer findCategoryId(
            Connection con, String category) throws SQLException {

        if (category == null || category.isBlank()) {
            return null;
        }

        String sql = """
            SELECT id
            FROM categories
            WHERE LOWER(name) = LOWER(?)
            LIMIT 1
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null;
    }

    // Handle nullable coordinates
    private void setCoordinate(
            PreparedStatement ps, int index, Double value)
            throws SQLException {

        if (value == null) {
            ps.setNull(index, Types.DOUBLE);
        } else {
            ps.setDouble(index, value);
        }
    }

    // Map database row to Item object
    private Item mapItem(ResultSet rs) throws SQLException {

        Item item = new Item();

        item.setItemId(rs.getLong("id"));
        item.setOwnerId(rs.getLong("owner_id"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setCategory(rs.getString("category_name"));
        item.setRentalPrice(rs.getBigDecimal("price_per_day"));
        item.setImagePath(rs.getString("image_url"));

        item.setPincode(rs.getString("pincode"));
        item.setDirections(rs.getString("directions"));

        item.setItemStatus(
                rs.getBoolean("available")
                        ? "AVAILABLE" : "UNAVAILABLE");

        double latitude = rs.getDouble("latitude");
        if (!rs.wasNull()) {
            item.setLatitude(latitude);
        }

        double longitude = rs.getDouble("longitude");
        if (!rs.wasNull()) {
            item.setLongitude(longitude);
        }

        item.setPlatformFee(PLATFORM_FEE);

        return item;
    }
}
