// ```java
package com.rentinstead.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Item {

    private long itemId;
    private long ownerId;
    private String title;
    private String category;
    private String itemCondition;
    private String description;

    // Owner's daily rental price
    private BigDecimal rentalPrice;

    // Platform fee per day
    private BigDecimal platformFee;

    private BigDecimal securityDeposit;

    // Pickup location information
    private String location;
    private String pincode;
    private String directions;

    // Coordinates for nearby search
    private Double latitude;
    private Double longitude;

    private String imagePath;
    private String itemStatus;
    private LocalDateTime createdAt;

    public Item() {
    }

    // Existing constructor retained for compatibility
    public Item(
            long ownerId,
            String title,
            String category,
            String itemCondition,
            String description,
            BigDecimal rentalPrice,
            BigDecimal securityDeposit,
            String location,
            String imagePath
    ) {
        this.ownerId = ownerId;
        this.title = title;
        this.category = category;
        this.itemCondition = itemCondition;
        this.description = description;
        this.rentalPrice = rentalPrice;
        this.platformFee = new BigDecimal("50.00");
        this.securityDeposit = securityDeposit;
        this.location = location;
        this.imagePath = imagePath;
        this.itemStatus = "AVAILABLE";
    }

    // Constructor including pickup location details
    public Item(
            long ownerId,
            String title,
            String category,
            String itemCondition,
            String description,
            BigDecimal rentalPrice,
            BigDecimal securityDeposit,
            String location,
            String pincode,
            String directions,
            Double latitude,
            Double longitude,
            String imagePath
    ) {
        this(
                ownerId,
                title,
                category,
                itemCondition,
                description,
                rentalPrice,
                securityDeposit,
                location,
                imagePath
        );

        this.pincode = pincode;
        this.directions = directions;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getCustomerDailyPrice() {
        BigDecimal ownerRent = rentalPrice == null
                ? BigDecimal.ZERO : rentalPrice;

        BigDecimal fee = platformFee == null
                ? new BigDecimal("50.00") : platformFee;

        return ownerRent.add(fee);
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(String itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(BigDecimal rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }

    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
