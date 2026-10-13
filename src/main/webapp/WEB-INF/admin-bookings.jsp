```jsp
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%!
    private String escapeHtml(Object value) {
        if (value == null) return "";
        return String.valueOf(value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    List<Map<String, Object>> bookings =
            (List<Map<String, Object>>) request.getAttribute("bookings");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Bookings | RentInstead Admin</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #F5F7FB;
            color: #1f2937;
        }

        header {
            background: #1D4ED8;
            color: white;
            padding: 20px 5%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 12px;
        }

        header h1 {
            margin: 0;
            font-size: 23px;
        }

        header a {
            color: white;
            text-decoration: none;
            font-weight: bold;
        }

        main {
            max-width: 1250px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .back-link {
            display: inline-block;
            margin-bottom: 18px;
            color: #1D4ED8;
            text-decoration: none;
            font-weight: bold;
        }

        .heading {
            margin-bottom: 20px;
        }

        .heading h2 {
            margin: 0 0 8px;
        }

        .heading p {
            margin: 0;
            color: #64748b;
        }

        .table-card {
            background: white;
            border-radius: 14px;
            padding: 20px;
            overflow-x: auto;
            box-shadow: 0 4px 18px rgba(15, 23, 42, 0.06);
        }

        table {
            width: 100%;
            min-width: 1000px;
            border-collapse: collapse;
        }

        th, td {
            text-align: left;
            padding: 13px 12px;
            border-bottom: 1px solid #e5e7eb;
            font-size: 13px;
            white-space: nowrap;
        }

        th {
            background: #eff6ff;
            color: #1e40af;
        }

        tbody tr:hover {
            background: #f8fafc;
        }

        .badge {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
        }

        .pending {
            background: #fef3c7;
            color: #92400e;
        }

        .approved, .active, .completed {
            background: #dcfce7;
            color: #166534;
        }

        .rejected, .cancelled {
            background: #fee2e2;
            color: #991b1b;
        }

        .empty {
            text-align: center;
            padding: 35px;
            color: #64748b;
        }

        @media (max-width: 600px) {
            main {
                margin: 20px auto;
                padding: 0 12px;
            }

            .table-card {
                padding: 12px;
            }

            header {
                padding: 16px;
            }
        }
    </style>
    <link rel="icon" type="image/svg+xml" href="/RentInstead/favicon.svg">
</head>

<body>

<header>
    <h1>RentInstead Admin</h1>
    <a href="<%= request.getContextPath() %>/admin-dashboard">
        Admin Dashboard
    </a>
</header>

<main>
    <a class="back-link"
       href="<%= request.getContextPath() %>/admin-dashboard">
        &larr; Back to Dashboard
    </a>

    <div class="heading">
        <h2>Manage Bookings</h2>
        <p>View rental booking details and their current status.</p>
    </div>

    <section class="table-card">

        <% if (bookings == null || bookings.isEmpty()) { %>

            <div class="empty">
                <h3>No bookings found</h3>
                <p>Rental bookings will appear here.</p>
            </div>

        <% } else { %>

            <table>
                <thead>
                    <tr>
                        <th>Booking ID</th>
                        <th>Item</th>
                        <th>Renter</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Total Amount</th>
                        <th>Status</th>
                        <th>Booked On</th>
                    </tr>
                </thead>

                <tbody>
                    <% for (Map<String, Object> booking : bookings) {
                        Object statusValue = booking.get("bookingStatus");

                        String status = statusValue == null
                                ? "UNKNOWN"
                                : statusValue.toString();

                        String statusClass = status.toLowerCase()
                                .replaceAll("[^a-z]", "");
                    %>

                    <tr>
                        <td><%= escapeHtml(booking.get("bookingId")) %></td>
                        <td><%= escapeHtml(booking.get("itemTitle")) %></td>
                        <td><%= escapeHtml(booking.get("renterName")) %></td>
                        <td><%= escapeHtml(booking.get("startDate")) %></td>
                        <td><%= escapeHtml(booking.get("endDate")) %></td>
                        <td>₹<%= escapeHtml(booking.get("totalAmount")) %></td>
                        <td>
                            <span class="badge <%= statusClass %>">
                                <%= escapeHtml(status) %>
                            </span>
                        </td>
                        <td><%= escapeHtml(booking.get("createdAt")) %></td>
                    </tr>

                    <% } %>
                </tbody>
            </table>

        <% } %>

    </section>
</main>

</body>
</html>
```