CREATE DATABASE IF NOT EXISTS rentinstead
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE rentinstead;

-- 1. Users
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    account_status ENUM('ACTIVE', 'BLOCKED', 'INACTIVE')
        NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

-- 2. Rental items
CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(80) NOT NULL,
    item_condition ENUM('NEW', 'LIKE_NEW', 'GOOD', 'FAIR')
        NOT NULL DEFAULT 'GOOD',
    description TEXT,
    rental_price DECIMAL(10,2) NOT NULL,
    security_deposit DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    location VARCHAR(150) NOT NULL,
    image_path VARCHAR(255),
    item_status ENUM('AVAILABLE', 'RENTED', 'INACTIVE')
        NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_items_owner
        FOREIGN KEY (owner_id) REFERENCES users(user_id),

    CONSTRAINT chk_items_price
        CHECK (rental_price >= 0 AND security_deposit >= 0),

    INDEX idx_items_category (category),
    INDEX idx_items_status (item_status)
);

-- 3. Rental bookings
CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_id BIGINT NOT NULL,
    renter_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    rental_days INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    booking_status ENUM(
        'PENDING',
        'APPROVED',
        'REJECTED',
        'ACTIVE',
        'COMPLETED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bookings_item
        FOREIGN KEY (item_id) REFERENCES items(item_id),

    CONSTRAINT fk_bookings_renter
        FOREIGN KEY (renter_id) REFERENCES users(user_id),

    CONSTRAINT chk_booking_dates
        CHECK (end_date >= start_date),

    CONSTRAINT chk_booking_amount
        CHECK (rental_days > 0 AND total_amount >= 0),

    INDEX idx_bookings_renter (renter_id),
    INDEX idx_bookings_item_dates (item_id, start_date, end_date)
);

-- 4. Wishlist
CREATE TABLE IF NOT EXISTS wishlist (
    wishlist_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_wishlist_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_wishlist_item
        FOREIGN KEY (item_id) REFERENCES items(item_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_wishlist_user_item
        UNIQUE (user_id, item_id)
);

-- 5. Notifications
CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE,

    INDEX idx_notifications_user (user_id, is_read)
);

-- 6. Contact messages
CREATE TABLE IF NOT EXISTS contact_messages (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    subject VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_contact_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE SET NULL
);