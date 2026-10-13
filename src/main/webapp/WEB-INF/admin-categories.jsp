```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="javax.servlet.http.HttpServletResponse" %>

<%
    if (session == null
            || session.getAttribute("userId") == null
            || !"ADMIN".equals(session.getAttribute("userRole"))) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required.");
        return;
    }

    List<Map<String, Object>> categories =
            (List<Map<String, Object>>) request.getAttribute("categories");

    if (categories == null) {
        categories = java.util.Collections.emptyList();
    }

    String contextPath = request.getContextPath();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Categories | RentInstead Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            background: #F5F7FB;
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
            background: #1D4ED8;
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

        .category-icon {
            display: inline-flex;
            width: 38px;
            height: 38px;
            align-items: center;
            justify-content: center;
            background: #EFF6FF;
            color: #1D4ED8;
            border-radius: 10px;
            margin-right: 12px;
            font-weight: bold;
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
    <a class="active" href="<%= contextPath %>/admin-categories">Categories</a>
    <a href="<%= contextPath %>/logout">Logout</a>
</aside>

<main class="main">

    <div class="mb-4">
        <h2 class="fw-bold mb-1">Item Categories</h2>
        <p class="text-secondary mb-0">
            Manage categories assigned to rental items.
        </p>
    </div>

    <section class="panel">

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h5 class="fw-bold mb-0">All Categories</h5>
            <span class="badge text-bg-primary">
                <%= categories.size() %> categories
            </span>
        </div>

        <% if (categories.isEmpty()) { %>

            <div class="text-center py-5">
                <h5>No categories found</h5>
                <p class="text-secondary mb-0">
                    Add an item with a category to see it here.
                </p>
            </div>

        <% } else { %>

            <div class="table-responsive">
                <table class="table align-middle">
                    <thead>
                    <tr>
                        <th>Category</th>
                        <th>Listed Items</th>
                        <th>Actions</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% for (Map<String, Object> category : categories) {
                        Object categoryName = category.get("name");
                        String name = categoryName == null
                                ? ""
                                : categoryName.toString();
                    %>
                        <tr>
                            <td>
                                <span class="category-icon">C</span>
                                <strong>
                                    <%= name.isEmpty() ? "Uncategorized" : name %>
                                </strong>
                            </td>

                            <td>
                                <span class="badge text-bg-light border">
                                    <%= category.get("itemCount") %> items
                                </span>
                            </td>

                            <td>
                                <div class="d-flex flex-wrap gap-2">

                                    <a class="btn btn-sm btn-outline-primary"
                                       href="<%= contextPath %>/admin-category-items?category=<%= java.net.URLEncoder.encode(name, "UTF-8") %>">
                                        View Items
                                    </a>

                                    <% if (!name.isEmpty()) { %>

                                        <button type="button"
                                                class="btn btn-sm btn-outline-secondary"
                                                onclick="editCategory(this)"
                                                data-category="<%= name.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") %>">
                                            Rename
                                        </button>

                                        <form method="post"
                                              action="<%= contextPath %>/admin-delete-category"
                                              onsubmit="return confirm('Clear this category from all its items? The items themselves will not be deleted.');">

                                            <input type="hidden"
                                                   name="category"
                                                   value="<%= name.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;") %>">

                                            <button type="submit"
                                                    class="btn btn-sm btn-outline-danger">
                                                Clear Category
                                            </button>
                                        </form>

                                    <% } %>
                                </div>
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

<div class="modal fade" id="renameModal" tabindex="-1"
     aria-labelledby="renameModalLabel" aria-hidden="true">

    <div class="modal-dialog">
        <div class="modal-content">

            <form method="post" action="<%= contextPath %>/admin-update-category">
                <div class="modal-header">
                    <h5 class="modal-title" id="renameModalLabel">
                        Rename Category
                    </h5>
                    <button type="button" class="btn-close"
                            data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body">
                    <label for="oldCategory" class="form-label">
                        Current category
                    </label>
                    <input type="text" id="oldCategory" name="oldCategory"
                           class="form-control mb-3" readonly required>

                    <label for="newCategory" class="form-label">
                        New category name
                    </label>
                    <input type="text" id="newCategory" name="newCategory"
                           class="form-control" maxlength="100"
                           required>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-light"
                            data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        Save Changes
                    </button>
                </div>
            </form>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
    function editCategory(button) {
        const category = button.getAttribute("data-category");
        document.getElementById("oldCategory").value = category;
        document.getElementById("newCategory").value = category;

        bootstrap.Modal.getOrCreateInstance(
            document.getElementById("renameModal")
        ).show();
    }
</script>

</body>
</html>
```