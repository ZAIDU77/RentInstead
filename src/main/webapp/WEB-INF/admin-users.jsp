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
    List<Map<String, Object>> users =
            (List<Map<String, Object>>) request.getAttribute("users");

    Object currentUserId = session.getAttribute("userId");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users | RentInstead Admin</title>

    <style>
        * { box-sizing: border-box; }

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

        header h1 { margin: 0; font-size: 23px; }

        header a {
            color: white;
            text-decoration: none;
            font-weight: bold;
        }

        main {
            max-width: 1200px;
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

        .page-heading { margin-bottom: 20px; }
        .page-heading h2 { margin: 0 0 8px; }
        .page-heading p { margin: 0; color: #64748b; }

        .table-card {
            background: white;
            border-radius: 14px;
            padding: 20px;
            box-shadow: 0 4px 18px rgba(15, 23, 42, 0.06);
            overflow-x: auto;
        }

        table {
            width: 100%;
            min-width: 1050px;
            border-collapse: collapse;
        }

        th, td {
            text-align: left;
            padding: 14px 12px;
            border-bottom: 1px solid #e5e7eb;
            font-size: 14px;
        }

        th {
            background: #eff6ff;
            color: #1e40af;
        }

        tbody tr:hover { background: #f8fafc; }

        .badge {
            display: inline-block;
            padding: 5px 9px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
        }

        .active {
            background: #dcfce7;
            color: #166534;
        }

        .blocked, .inactive {
            background: #fee2e2;
            color: #991b1b;
        }

        .actions {
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 8px;
        }

        .action-form {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        select, button {
            padding: 8px 10px;
            border-radius: 7px;
            font-size: 13px;
        }

        select {
            border: 1px solid #cbd5e1;
            background: white;
        }

        button {
            border: none;
            color: white;
            cursor: pointer;
            font-weight: bold;
        }

        .update-button { background: #1D4ED8; }
        .update-button:hover { background: #1e40af; }

        .deactivate-button { background: #dc2626; }
        .deactivate-button:hover { background: #991b1b; }

        .protected {
            color: #64748b;
            font-size: 12px;
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

            .table-card { padding: 12px; }
            header { padding: 16px; }
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

    <div class="page-heading">
        <h2>Manage Users</h2>
        <p>View registered users and manage their account status.</p>
    </div>

    <section class="table-card">

        <% if (users == null || users.isEmpty()) { %>

            <div class="empty">
                <h3>No users found</h3>
                <p>Registered users will appear here.</p>
            </div>

        <% } else { %>

            <table>
                <thead>
                    <tr>
                        <th>User ID</th>
                        <th>Full Name</th>
                        <th>Phone</th>
                        <th>Role</th>
                        <th>Account Status</th>
                        <th>Joined On</th>
                        <th>Actions</th>
                    </tr>
                </thead>

                <tbody>
                    <% for (Map<String, Object> user : users) {

                        Object statusValue = user.get("accountStatus");

                        String status = statusValue == null
                                ? "UNKNOWN"
                                : statusValue.toString();

                        String statusClass = status.toLowerCase();

                        boolean isSelf = currentUserId instanceof Number
                                && user.get("userId") instanceof Number
                                && ((Number) currentUserId).longValue()
                                == ((Number) user.get("userId")).longValue();

                        boolean isAdmin = "ADMIN".equalsIgnoreCase(
                                String.valueOf(user.get("role"))
                        );
                    %>

                    <tr>
                        <td><%= escapeHtml(user.get("userId")) %></td>
                        <td><%= escapeHtml(user.get("fullName")) %></td>
                        <td><%= escapeHtml(user.get("phone")) %></td>
                        <td><%= escapeHtml(user.get("role")) %></td>

                        <td>
                            <span class="badge <%= statusClass %>">
                                <%= escapeHtml(status) %>
                            </span>
                        </td>

                        <td><%= escapeHtml(user.get("createdAt")) %></td>

                        <td>
                            <% if (isSelf || isAdmin) { %>

                                <span class="protected">
                                    Protected account
                                </span>

                            <% } else if (
                                    "ACTIVE".equals(status)
                                    || "BLOCKED".equals(status)) { %>

                                <div class="actions">

                                    <form class="action-form"
                                          method="post"
                                          action="<%= request.getContextPath() %>/admin-update-user"
                                          onsubmit="return confirm('Change this user account status?');">

                                        <input type="hidden"
                                               name="userId"
                                               value="<%= escapeHtml(user.get("userId")) %>">

                                        <select name="status" required>
                                            <option value="ACTIVE"
                                                <%= "ACTIVE".equals(status)
                                                        ? "selected" : "" %>>
                                                Active
                                            </option>

                                            <option value="BLOCKED"
                                                <%= "BLOCKED".equals(status)
                                                        ? "selected" : "" %>>
                                                Blocked
                                            </option>
                                        </select>

                                        <button class="update-button"
                                                type="submit">
                                            Update
                                        </button>
                                    </form>

                                    <form class="action-form"
                                          method="post"
                                          action="<%= request.getContextPath() %>/admin-delete-user"
                                          onsubmit="return confirm('Deactivate this user account?');">

                                        <input type="hidden"
                                               name="userId"
                                               value="<%= escapeHtml(user.get("userId")) %>">

                                        <button class="deactivate-button"
                                                type="submit">
                                            Deactivate
                                        </button>
                                    </form>

                                </div>

                            <% } else { %>

                                <span class="protected">
                                    Account inactive
                                </span>

                            <% } %>
                        </td>
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