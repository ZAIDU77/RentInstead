jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%
    HttpSession adminSession = request.getSession(false);

    if (adminSession == null
            || adminSession.getAttribute("userId") == null
            || !"ADMIN".equals(adminSession.getAttribute("userRole"))) {
        response.sendError(
                HttpServletResponse.SC_FORBIDDEN,
                "Admin access required."
        );
        return;
    }

    long totalUsers = request.getAttribute("totalUsers") instanceof Number
            ? ((Number) request.getAttribute("totalUsers")).longValue() : 0L;

    long activeUsers = request.getAttribute("activeUsers") instanceof Number
            ? ((Number) request.getAttribute("activeUsers")).longValue() : 0L;

    long totalItems = request.getAttribute("totalItems") instanceof Number
            ? ((Number) request.getAttribute("totalItems")).longValue() : 0L;

    long totalBookings = request.getAttribute("totalBookings") instanceof Number
            ? ((Number) request.getAttribute("totalBookings")).longValue() : 0L;

    long pendingBookings = request.getAttribute("pendingBookings") instanceof Number
            ? ((Number) request.getAttribute("pendingBookings")).longValue() : 0L;

    long newMessages = request.getAttribute("newMessages") instanceof Number
            ? ((Number) request.getAttribute("newMessages")).longValue() : 0L;

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Admin Dashboard | RentInstead</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>

    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Bricolage+Grotesque:wght@500;600;700&display=swap"
          rel="stylesheet">

    <style>
        * {
            box-sizing: border-box;
        }

        :root {
            --primary: #1D4ED8;
            --orange: #C2410C;
            --background: #F5F7FB;
            --text: #172033;
            --muted: #647084;
            --border: #E5EAF2;
        }

        body {
            margin: 0;
            background: var(--background);
            color: var(--text);
            font-family: "DM Sans", sans-serif;
        }

        a {
            text-decoration: none;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 16px;
            padding: 18px 5%;
            background: white;
            border-bottom: 1px solid var(--border);
        }

        .brand {
            color: var(--primary);
            font: 700 25px "Bricolage Grotesque", sans-serif;
        }

        .top-actions {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .top-link {
            padding: 10px 14px;
            border-radius: 8px;
            color: var(--primary);
            background: #EFF6FF;
            font-weight: 700;
        }

        .logout {
            color: var(--orange);
            background: #FFF1ED;
        }

        .layout {
            width: min(1200px, 92%);
            margin: 35px auto;
        }

        .heading {
            margin-bottom: 26px;
        }

        h1 {
            margin: 0 0 8px;
            font: 700 34px "Bricolage Grotesque", sans-serif;
        }

        .subtitle {
            margin: 0;
            color: var(--muted);
            line-height: 1.6;
        }

        .stats {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 18px;
        }

        .stat {
            padding: 24px;
            background: white;
            border: 1px solid var(--border);
            border-radius: 14px;
            box-shadow: 0 8px 25px rgba(23, 32, 51, .04);
        }

        .stat-label {
            color: var(--muted);
            font-size: 14px;
        }

        .stat-number {
            margin-top: 12px;
            color: var(--primary);
            font: 700 32px "Bricolage Grotesque", sans-serif;
            overflow-wrap: anywhere;
        }

        .management {
            margin-top: 30px;
        }

        .section-title {
            margin: 0 0 16px;
            font: 700 23px "Bricolage Grotesque", sans-serif;
        }

        .modules {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 16px;
        }

        .module {
            display: block;
            padding: 22px;
            background: white;
            border: 1px solid var(--border);
            border-radius: 13px;
            color: var(--text);
            transition: transform .2s, box-shadow .2s;
        }

        .module:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 25px rgba(23, 32, 51, .08);
        }

        .module-icon {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 42px;
            height: 42px;
            margin-bottom: 15px;
            border-radius: 11px;
            background: #EFF6FF;
            color: var(--primary);
            font-size: 20px;
            font-weight: 700;
        }

        .module h3 {
            margin: 0 0 7px;
            font-size: 17px;
        }

        .module p {
            margin: 0;
            color: var(--muted);
            font-size: 14px;
            line-height: 1.6;
        }

        .footer {
            margin-top: 32px;
            padding: 20px 0;
            border-top: 1px solid var(--border);
            color: var(--muted);
            font-size: 13px;
            text-align: center;
        }

        @media (max-width: 850px) {
            .stats,
            .modules {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 520px) {
            .topbar {
                align-items: flex-start;
                flex-direction: column;
            }

            .top-actions {
                width: 100%;
                flex-wrap: wrap;
            }

            .top-link {
                text-align: center;
            }

            .layout {
                margin: 26px auto;
            }

            h1 {
                font-size: 28px;
            }

            .stats,
            .modules {
                grid-template-columns: 1fr;
            }

            .stat {
                padding: 20px;
            }

            .stat-number {
                font-size: 28px;
            }
        }
    </style>
    <link rel="icon" type="image/svg+xml" href="/RentInstead/favicon.svg">
</head>

<body>

<header class="topbar">
    <a class="brand" href="<%= contextPath %>/index.html">
        RentInstead
    </a>

    <div class="top-actions">
        <a class="top-link" href="<%= contextPath %>/index.html">
            View Website
        </a>

        <a class="top-link" href="<%= contextPath %>/html/dashboard/profile.html">
            My Profile
        </a>

        <a class="top-link logout" href="<%= contextPath %>/logout">
            Logout
        </a>
    </div>
</header>

<main class="layout">

    <section class="heading">
        <h1>Admin Dashboard</h1>
        <p class="subtitle">
            Monitor your marketplace and manage users, listings and bookings.
        </p>
    </section>

    <section class="stats">

        <article class="stat">
            <div class="stat-label">Total Users</div>
            <div class="stat-number"><%= totalUsers %></div>
        </article>

        <article class="stat">
            <div class="stat-label">Active Users</div>
            <div class="stat-number"><%= activeUsers %></div>
        </article>

        <article class="stat">
            <div class="stat-label">Total Items</div>
            <div class="stat-number"><%= totalItems %></div>
        </article>

        <article class="stat">
            <div class="stat-label">Total Bookings</div>
            <div class="stat-number"><%= totalBookings %></div>
        </article>

        <article class="stat">
            <div class="stat-label">Pending Bookings</div>
            <div class="stat-number"><%= pendingBookings %></div>
        </article>

        <article class="stat">
            <div class="stat-label">New Contact Messages</div>
            <div class="stat-number"><%= newMessages %></div>
        </article>

    </section>

    <section class="management">
        <h2 class="section-title">Management</h2>

        <div class="modules">

            <a class="module" href="<%= contextPath %>/admin-users">
                <span class="module-icon">U</span>
                <h3>Manage Users</h3>
                <p>View registered users and manage account status.</p>
            </a>

            <a class="module" href="<%= contextPath %>/admin-bookings">
                <span class="module-icon">B</span>
                <h3>Manage Bookings</h3>
                <p>Review rental requests and update booking status.</p>
            </a>

            <a class="module" href="<%= contextPath %>/admin-messages">
                <span class="module-icon">M</span>
                <h3>Contact Messages</h3>
                <p>Read and manage messages submitted by users.</p>
            </a>

            <a class="module" href="<%= contextPath %>/admin-reports">
                <span class="module-icon">R</span>
                <h3>Reports</h3>
                <p>Review marketplace statistics and export reports.</p>
            </a>

            <a class="module" href="<%= contextPath %>/admin-categories">
                <span class="module-icon">C</span>
                <h3>Categories</h3>
                <p>View categories and manage category assignments.</p>
            </a>

            <a class="module" href="<%= contextPath %>/admin-dashboard">
                <span class="module-icon">D</span>
                <h3>Refresh Dashboard</h3>
                <p>Reload the latest marketplace statistics.</p>
            </a>

        </div>
    </section>

    <footer class="footer">
        RentInstead Admin Panel &copy; 2026
    </footer>

</main>

</body>
</html>
