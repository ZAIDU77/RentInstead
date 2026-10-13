```jsp
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>

<%
    Map<String, Object> reports =
            (Map<String, Object>) request.getAttribute("reports");

    if (reports == null) {
        reports = java.util.Collections.emptyMap();
    }

    Object revenueValue = reports.get("totalRevenue");

    String revenue = revenueValue == null
            ? "0.00"
            : String.valueOf(revenueValue);

    Object currentRole = session.getAttribute("userRole");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Platform Reports | RentInstead Admin</title>

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
            max-width: 1150px;
            margin: 30px auto;
            padding: 0 20px;
        }

        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #1D4ED8;
            text-decoration: none;
            font-weight: bold;
        }

        .page-heading {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 16px;
            flex-wrap: wrap;
            margin-bottom: 25px;
        }

        .page-heading h2 {
            margin: 0 0 8px;
        }

        .page-heading p {
            color: #64748b;
            margin: 0;
        }

        .export-button {
            display: inline-block;
            background: #C2410C;
            color: white;
            padding: 12px 17px;
            border-radius: 9px;
            text-decoration: none;
            font-weight: bold;
        }

        .export-button:hover {
            background: #9a3412;
        }

        .report-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 18px;
        }

        .report-card {
            background: white;
            padding: 24px;
            border-radius: 14px;
            box-shadow: 0 4px 18px rgba(15, 23, 42, 0.06);
            border-top: 4px solid #1D4ED8;
        }

        .report-card h3 {
            margin: 0 0 16px;
            color: #64748b;
            font-size: 14px;
        }

        .report-value {
            font-size: 30px;
            font-weight: bold;
            overflow-wrap: anywhere;
        }

        .revenue-card {
            border-top-color: #C2410C;
        }

        .report-note {
            margin-top: 25px;
            background: #eff6ff;
            color: #1e40af;
            padding: 16px;
            border-radius: 10px;
            line-height: 1.6;
            font-size: 14px;
        }

        .footer {
            text-align: center;
            color: #64748b;
            padding: 25px 12px;
            font-size: 13px;
        }

        @media (max-width: 800px) {
            .report-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 500px) {
            main {
                margin: 20px auto;
                padding: 0 12px;
            }

            header {
                padding: 16px;
            }

            .report-grid {
                grid-template-columns: 1fr;
            }

            .report-card {
                padding: 20px;
            }

            .report-value {
                font-size: 26px;
            }

            .export-button {
                width: 100%;
                text-align: center;
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

    <section class="page-heading">
        <div>
            <h2>Platform Reports</h2>
            <p>Overview of users, listings, bookings and messages.</p>
        </div>

        <a class="export-button"
           href="<%= request.getContextPath() %>/admin-export-reports">
            &#8595; Export CSV
        </a>
    </section>

    <section class="report-grid">

        <article class="report-card">
            <h3>Total Users</h3>
            <div class="report-value">
                <%= reports.getOrDefault("totalUsers", 0L) %>
            </div>
        </article>

        <article class="report-card">
            <h3>Active Users</h3>
            <div class="report-value">
                <%= reports.getOrDefault("activeUsers", 0L) %>
            </div>
        </article>

        <article class="report-card">
            <h3>Total Items Listed</h3>
            <div class="report-value">
                <%= reports.getOrDefault("totalItems", 0L) %>
            </div>
        </article>

        <article class="report-card">
            <h3>Total Bookings</h3>
            <div class="report-value">
                <%= reports.getOrDefault("totalBookings", 0L) %>
            </div>
        </article>

        <article class="report-card">
            <h3>Pending Bookings</h3>
            <div class="report-value">
                <%= reports.getOrDefault("pendingBookings", 0L) %>
            </div>
        </article>

        <article class="report-card">
            <h3>Contact Messages</h3>
            <div class="report-value">
                <%= reports.getOrDefault("totalMessages", 0L) %>
            </div>
        </article>

        <article class="report-card revenue-card">
            <h3>Booking Amount Total</h3>
            <div class="report-value">
                ₹<%= revenue %>
            </div>
        </article>

    </section>

    <div class="report-note">
        <strong>Note:</strong>
        Booking amount total is the sum of bookings with APPROVED,
        ACTIVE or COMPLETED status. This amount is not necessarily
        the platform's actual profit or collected revenue.
    </div>

</main>

<footer class="footer">
    RentInstead Admin Panel &copy; 2026
</footer>

</body>
</html>
```