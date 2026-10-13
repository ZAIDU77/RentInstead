package com.rentinstead.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Booking {

    private long bookingId;
    private long itemId;
    private long renterId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int rentalDays;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private LocalDateTime createdAt;

    public Booking() {
    }

    public Booking(
            long itemId,
            long renterId,
            LocalDate startDate,
            LocalDate endDate,
            int rentalDays,
            BigDecimal totalAmount
    ) {
        this.itemId = itemId;
        this.renterId = renterId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.rentalDays = rentalDays;
        this.totalAmount = totalAmount;
        this.bookingStatus = "PENDING";
    }

    public long getBookingId() {
        return bookingId;
    }

    public void setBookingId(long bookingId) {
        this.bookingId = bookingId;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getRenterId() {
        return renterId;
    }

    public void setRenterId(long renterId) {
        this.renterId = renterId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(int rentalDays) {
        this.rentalDays = rentalDays;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}