<!-- ```jsp -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.rentinstead.model.Item" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.temporal.ChronoUnit" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>
<%
    Item item = (Item) request.getAttribute("item");

    if (item == null) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Item not found.");
        return;
    }

    String title = item.getTitle() == null ? "Rental Item" : item.getTitle();
    String category = item.getCategory() == null ? "General" : item.getCategory();
    String condition = item.getItemCondition() == null ? "Not specified" : item.getItemCondition();
    String description = item.getDescription() == null ? "No description available." : item.getDescription();
    String location = item.getLocation() == null ? "Location not specified" : item.getLocation();

    BigDecimal price = item.getRentalPrice() == null
            ? BigDecimal.ZERO : item.getRentalPrice();

    BigDecimal deposit = item.getSecurityDeposit() == null
            ? BigDecimal.ZERO : item.getSecurityDeposit();

    String safeTitle = title.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    String safeCategory = category.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    String safeCondition = condition.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    String safeDescription = description.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    String safeLocation = location.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");

    String imagePath = item.getImagePath() == null ? "" : item.getImagePath().trim();
    String safeImagePath = imagePath.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= safeTitle %> | RentInstead</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link rel="icon" type="image/svg+xml" href="<%= request.getContextPath() %>/favicon.svg">

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            background: #f5f7fb;
            color: #172033;
            font-family: Arial, sans-serif;
        }

        .topbar {
            padding: 18px 6%;
            background: white;
            border-bottom: 1px solid #e8edf5;
        }

        .brand {
            color: #1d4ed8;
            text-decoration: none;
            font-size: 26px;
            font-weight: 800;
        }

        .container-main {
            width: min(1050px, 92%);
            margin: 32px auto 60px;
        }

        .back {
            display: inline-block;
            margin-bottom: 20px;
            color: #1d4ed8;
            text-decoration: none;
            font-weight: 700;
        }

        .product-card, .booking-card {
            overflow: hidden;
            background: white;
            border: 1px solid #e5eaf2;
            border-radius: 18px;
            box-shadow: 0 10px 32px #1720330c;
        }

        .product-card {
            display: grid;
            grid-template-columns: 1fr 1fr;
        }

        .image-panel {
            min-height: 360px;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
            background: #edf2ff;
        }

        .image-panel img {
            width: 100%;
            max-height: 420px;
            object-fit: contain;
            border-radius: 12px;
        }

        .image-placeholder {
            color: #1d4ed8;
            font-size: 90px;
        }

        .details { padding: 32px; }

        .category {
            display: inline-block;
            padding: 7px 12px;
            border-radius: 20px;
            background: #eaf0ff;
            color: #1d4ed8;
            font-size: 13px;
            font-weight: 700;
        }

        h1 {
            margin: 18px 0 12px;
            font-size: clamp(25px, 4vw, 34px);
            font-weight: 800;
            overflow-wrap: anywhere;
        }

        .description {
            color: #647084;
            line-height: 1.8;
            overflow-wrap: anywhere;
            white-space: pre-wrap;
        }

        .info {
            margin-top: 20px;
            padding: 15px 0;
            border-top: 1px solid #e8edf5;
        }

        .info p { margin: 10px 0; overflow-wrap: anywhere; }

        .price {
            margin-top: 18px;
            color: #1d4ed8;
            font-size: 29px;
            font-weight: 800;
        }

        .muted { color: #647084; font-size: 14px; }

        .primary-btn {
            display: inline-block;
            width: 100%;
            padding: 13px 18px;
            border: 0;
            border-radius: 10px;
            background: #1d4ed8;
            color: white;
            text-align: center;
            text-decoration: none;
            font-weight: 700;
            cursor: pointer;
        }

        .primary-btn:hover {
            background: #1e40af;
            color: white;
        }

        .booking-card {
            margin-top: 28px;
            padding: clamp(20px, 4vw, 34px);
            scroll-margin-top: 20px;
        }

        .section-title {
            margin-bottom: 8px;
            font-size: 26px;
            font-weight: 800;
        }

        .form-label { font-weight: 700; }

        .form-control {
            min-height: 46px;
            border-radius: 9px;
        }

        .summary {
            margin-top: 22px;
            padding: 20px;
            border: 1px solid #dbeafe;
            border-radius: 12px;
            background: #f8faff;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            gap: 12px;
            margin: 10px 0;
        }

        .grand-total {
            padding-top: 12px;
            border-top: 1px solid #dbeafe;
            color: #1d4ed8;
            font-size: 23px;
            font-weight: 800;
        }

        .payment-card {
            margin-top: 22px;
            padding: 20px;
            border: 1px solid #e5eaf2;
            border-radius: 12px;
            text-align: center;
        }

        .payment-card img {
            display: block;
            width: 230px;
            height: 230px;
            max-width: 100%;
            margin: 16px auto;
            object-fit: contain;
        }

        .upi-id {
            display: inline-block;
            padding: 10px 14px;
            border-radius: 8px;
            background: #eff6ff;
            color: #1d4ed8;
            font-weight: 800;
            overflow-wrap: anywhere;
        }

        .notice {
            padding: 12px;
            border-radius: 9px;
            background: #fff7ed;
            color: #9a3412;
            font-size: 13px;
            line-height: 1.6;
        }

        footer {
            padding: 22px;
            background: white;
            border-top: 1px solid #e8edf5;
            color: #647084;
            text-align: center;
            font-size: 13px;
        }

        @media (max-width: 700px) {
            .container-main { margin-top: 22px; }
            .product-card { grid-template-columns: 1fr; }
            .image-panel { min-height: 220px; }
            .details { padding: 22px; }
            .section-title { font-size: 23px; }
        }
    </style>
</head>
<body>
<header class="topbar">
    <a class="brand" href="<%= request.getContextPath() %>/index.html">
        RentInstead
    </a>
</header>

<main class="container-main">
    <a class="back" href="<%= request.getContextPath() %>/html/items/browse.html">
        <i class="bi bi-arrow-left"></i> Back to items
    </a>

    <section class="product-card">
        <div class="image-panel">
            <% if (!imagePath.isEmpty()) { %>
                <img src="<%= safeImagePath %>"
                     alt="<%= safeTitle %>"
                     onerror="this.style.display='none';this.nextElementSibling.hidden=false;">
                <span class="image-placeholder" hidden>
                    <i class="bi bi-box-seam"></i>
                </span>
            <% } else { %>
                <span class="image-placeholder">
                    <i class="bi bi-box-seam"></i>
                </span>
            <% } %>
        </div>

        <div class="details">
            <span class="category"><%= safeCategory %></span>
            <h1><%= safeTitle %></h1>
            <p class="description"><%= safeDescription %></p>

            <div class="info">
                <p><strong>Condition:</strong> <%= safeCondition %></p>
                <p><strong>Location:</strong> <%= safeLocation %></p>
                <p><strong>Item ID:</strong> <%= item.getItemId() %></p>
                <p><strong>Availability:</strong> <span class="text-success fw-bold">Available</span></p>
            </div>

            <div class="price">₹<%= price.toPlainString() %> <span class="muted">/ day</span></div>
            <p class="muted mt-2">
                Security deposit: ₹<%= deposit.toPlainString() %>
            </p>

            <a class="primary-btn mt-3" href="#bookingForm">
                Book This Item
            </a>
        </div>
    </section>

    <section class="booking-card" id="bookingForm">
        <h2 class="section-title">Book This Item</h2>
        <p class="muted mb-4">
            Choose your rental dates and pickup location to calculate the amount.
        </p>

        <form id="rentalForm">
            <input type="hidden" id="itemId" value="<%= item.getItemId() %>">

            <div class="mb-3">
                <label for="pickupLocation" class="form-label">Pickup Location</label>
                <input type="text" id="pickupLocation" class="form-control"
                       maxlength="250" required
                       placeholder="Enter the location where you will collect the item">
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="startDate" class="form-label">Rental Start Date</label>
                    <input type="date" id="startDate" class="form-control" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label for="endDate" class="form-label">Return Date</label>
                    <input type="date" id="endDate" class="form-control" required>
                </div>
            </div>

            <div class="notice mb-3">
                The dates selected here are not yet checked against existing bookings.
                Confirm availability with the owner before paying.
            </div>

            <button type="submit" class="primary-btn">
                Calculate Total &amp; Generate QR
            </button>
        </form>

        <div class="summary" id="rentalSummary" hidden>
            <h3 class="h5 fw-bold mb-3">Booking Summary</h3>
            <div class="summary-row">
                <span>Item</span>
                <strong><%= safeTitle %></strong>
            </div>
            <div class="summary-row">
                <span>Rental period</span>
                <strong id="dateRange">—</strong>
            </div>
            <div class="summary-row">
                <span>Rental days</span>
                <strong id="rentalDays">0</strong>
            </div>
            <div class="summary-row">
                <span>Rent per day</span>
                <strong>₹<%= price.toPlainString() %></strong>
            </div>
            <div class="summary-row">
                <span>Rental amount</span>
                <strong id="totalRent">₹0.00</strong>
            </div>
            <div class="summary-row">
                <span>Security deposit (shown separately)</span>
                <strong>₹<%= deposit.toPlainString() %></strong>
            </div>
            <div class="summary-row grand-total">
                <span>Total rental payment</span>
                <span id="grandTotal">₹0.00</span>
            </div>
            <p class="muted mt-2 mb-0">
                The security deposit is not included in this rental QR amount.
            </p>
        </div>

        <div class="payment-card" id="paymentSection" hidden>
            <h3 class="h5 fw-bold">Pay with UPI</h3>
            <p class="muted">Scan this QR code in your UPI app.</p>

            <div class="upi-id">
                UPI ID: 9140159897@ibl
                <button type="button" class="btn btn-sm btn-link"
                        id="copyUpiButton">Copy</button>
            </div>

            <img id="paymentQR" alt="UPI payment QR code">

            <p class="fw-bold" id="qrAmount"></p>
<p class="fw-bold text-danger" id="qrCountdown">QR expires in 02:30</p>
<p class="notice" id="qrExpired" hidden>
    QR expired! Please generate a new QR to pay.
</p>

            <a id="openUpiButton" class="primary-btn" href="#">
                Open UPI App
            </a>

            <p class="muted mt-3">
                Booking is not confirmed just by displaying or scanning this QR.
            </p>

            <div class="notice text-start mt-3">
                Payment verification and saving the booking to MySQL are not
                connected yet. Do not treat the booking as paid until it has
                been verified.
            </div>
        </div>
    </section>
</main>

<footer>
    © 2026 RentInstead. Access instead of ownership.
</footer>

<script>
(function () {
    "use strict";

    const form = document.getElementById("rentalForm");
    const startInput = document.getElementById("startDate");
    const endInput = document.getElementById("endDate");
    const locationInput = document.getElementById("pickupLocation");
    const summary = document.getElementById("rentalSummary");
    const paymentSection = document.getElementById("paymentSection");
    const pricePerDay = Number("<%= price.toPlainString() %>");
    const itemId = document.getElementById("itemId").value;
    const upiId = "9140159897@ibl";

    function localDateString(date) {
        return [
            date.getFullYear(),
            String(date.getMonth() + 1).padStart(2, "0"),
            String(date.getDate()).padStart(2, "0")
        ].join("-");
    }

    const today = localDateString(new Date());
    startInput.min = today;
    endInput.min = today;

    startInput.addEventListener("change", function () {
        endInput.min = startInput.value || today;
        if (endInput.value && endInput.value < startInput.value) {
            endInput.value = "";
        }
        summary.hidden = true;
        paymentSection.hidden = true;
    });

    endInput.addEventListener("change", function () {
        summary.hidden = true;
        paymentSection.hidden = true;
    });

    locationInput.addEventListener("input", function () {
        summary.hidden = true;
        paymentSection.hidden = true;
    });

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        const start = startInput.value;
        const end = endInput.value;
        const pickup = locationInput.value.trim();

        if (!pickup || !start || !end) {
            alert("Please enter the pickup location and both dates.");
            return;
        }

        if (start < today) {
            alert("The rental start date cannot be in the past.");
            return;
        }

        if (end <= start) {
            alert("Return date must be after the start date.");
            return;
        }

        if (!Number.isFinite(pricePerDay) || pricePerDay < 0) {
            alert("The item rental price is invalid.");
            return;
        }

        const s = start.split("-").map(Number);
        const e = end.split("-").map(Number);
        const startUTC = Date.UTC(s[0], s[1] - 1, s[2]);
        const endUTC = Date.UTC(e[0], e[1] - 1, e[2]);
        const days = Math.floor((endUTC - startUTC) / 86400000);

        if (days < 1 || days > 365) {
            alert("Please select a rental period between 1 and 365 days.");
            return;
        }

        const amount = (pricePerDay * days).toFixed(2);

        document.getElementById("dateRange").textContent = start + " to " + end;
        document.getElementById("rentalDays").textContent = String(days);
        document.getElementById("totalRent").textContent = "₹" + amount;
        document.getElementById("grandTotal").textContent = "₹" + amount;
        document.getElementById("qrAmount").textContent = "Amount: ₹" + amount;

        const upiParams = new URLSearchParams({
            pa: upiId,
            pn: "RentInstead",
            am: amount,
            cu: "INR",
            tn: "RentInstead item " + itemId
        });
        const upiLink = "upi://pay?" + upiParams.toString();

        document.getElementById("paymentQR").src =
            "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data="
            + encodeURIComponent(upiLink);

        document.getElementById("openUpiButton").href = upiLink;

        summary.hidden = false;
        paymentSection.hidden = false;
        startQrExpiryTimer();
        paymentSection.scrollIntoView({ behavior: "smooth", block: "start" });
    });

    document.getElementById("copyUpiButton").addEventListener("click", async function () {
        try {
            await navigator.clipboard.writeText(upiId);
            this.textContent = "Copied";
        } catch (error) {
            alert("UPI ID: " + upiId);
        }
    });
})();
</script>
</body>
</html>
