```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%
    if (session == null
            || session.getAttribute("userId") == null
            || !"ADMIN".equals(session.getAttribute("userRole"))) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
        return;
    }

    String selectedCategory =
            (String) request.getAttribute("selectedCategory");

    List<Map<String, Object>> items =
            (List<Map<String, Object>>) request.getAttribute("categoryItems");

    if (items == null) {
        items = java.util.Collections.emptyList();
    }

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Category Items | RentInstead Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        :root {
            --primary: #1D4ED8;
            --background: #F5F7FB;
        }

        body {
            margin: 0;
            background: var(--background);
            color: #172033;
            font-family: Arial, sans-serif;
        }

        .sidebar {
            width: 245px;
            min-height: 100vh;
            position: fixed;
            inset: 0 auto 0 0;
            background: #111827;
            padding: 28px 18px;
        }

        .brand {
            color: white;
            font-size: 23px;
            font-weight: 700;
            margin-bottom: 35px;
        }

        .sidebar a {
            display: block;
            color: #D1D5DB;
            text-decoration: none;
            padding: 12px 14px;
            border-radius: 9px;
            margin-bottom: 7px;
        }

        .sidebar a:hover,
        .sidebar a.active {
            background: var(--primary);
            color: white;
        }

        .main {
            margin-left: 245px;
            padding: 35px;
        }

        .panel {
            background: white;
            border-radius: 14px;
            padding: 24px;
            box-shadow: 0 5px 22px rgba(15, 23, 42, 0.05);
        }

        .category-label {
            display: inline-block;
            padding: 7px 12px;
            border-radius: 20px;
            background: #EFF6FF;
            color: var(--primary);
            font-weight: 600;
        }

        @media (max-width: 768px) {
            .sidebar {
                position: static;
                width: 100%;
                min-height: auto;
            }

            .brand {
                margin-bottom: 15px;
            }

            .main {
                margin-left: 0;
                padding: 20px 14px;
            }
        }
    </style>
    <link rel="icon" type="image/svg+xml" href="/RentInstead/favicon.svg">
</head>

<body>

<aside class="sidebar">
    <div class="brand">RentInstead</div>

    <a href="<%= contextPath %>/admin-dashboard">Dashboard</a>
    <a href="<%= contextPath %>/admin-users">Users</a>
    <a href="<%= contextPath %>/admin-bookings">Bookings</a>
    <a href="<%= contextPath %>/admin-messages">Messages</a>
    <a href="<%= contextPath %>/admin-reports">Reports</a>
    <a href="<%= contextPath %>/admin-categories">Categories</a>
    <a href="<%= contextPath %>/logout">Logout</a>
</aside>

<main class="main">

    <div class="mb-4">
        <a href="<%= contextPath %>/admin-categories"
           class="text-decoration-none">&larr; Back to Categories</a>

        <h2 class="fw-bold mt-3 mb-2">Category Items</h2>

        <p class="text-secondary">
            Category:
            <span class="category-label">
                <%= selectedCategory == null ? "Unknown" : selectedCategory %>
            </span>
        </p>
    </div>

    <section class="panel">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="fw-bold mb-0">Listed Items</h5>

            <span class="badge text-bg-primary">
                <%= items.size() %> items
            </span>
        </div>

        <% if (items.isEmpty()) { %>

            <div class="text-center py-5">
                <h5>No items found</h5>
                <p class="text-secondary mb-0">
                    This category currently has no listed items.
                </p>
            </div>

        <% } else { %>

            <div class="table-responsive">
                <table class="table align-middle">
                    <thead>
                    <tr>
                        <th>Item ID</th>
                        <th>Item Name</th>
                        <th>Category</th>
                        <th>Price / Day</th>
                        <th>Status</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% for (Map<String, Object> item : items) { %>
                        <tr>
                            <td>
                                <%= item.get("itemId") %>
                            </td>

                            <td class="fw-semibold">
                                <%= item.get("title") == null
                                        ? "Untitled item"
                                        : item.get("title") %>
                            </td>

                            <td>
                                <%= item.get("category") == null
                                        ? "Uncategorized"
                                        : item.get("category") %>
                            </td>

                            <td>
                                ₹<%= item.get("pricePerDay") == null
                                        ? "0.00"
                                        : item.get("pricePerDay") %>
                            </td>

                            <td>
                                <span class="badge text-bg-light border">
                                    <%= item.get("status") == null
                                            ? "Unknown"
                                            : item.get("status") %>
                                </span>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>

        <% } %>

    </section>

    <p class="text-secondary small mt-4">
        RentInstead Admin Panel &copy; 2026
    </p>

</main>

</body>
</html>
```