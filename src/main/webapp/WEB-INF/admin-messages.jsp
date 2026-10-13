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
    List<Map<String, Object>> messages =
            (List<Map<String, Object>>) request.getAttribute("messages");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contact Messages | RentInstead Admin</title>

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

        .message-card {
            background: white;
            padding: 22px;
            margin-bottom: 16px;
            border-radius: 14px;
            box-shadow: 0 4px 18px rgba(15, 23, 42, 0.06);
            overflow-wrap: anywhere;
        }

        .message-top {
            display: flex;
            justify-content: space-between;
            gap: 15px;
            flex-wrap: wrap;
            border-bottom: 1px solid #e5e7eb;
            padding-bottom: 14px;
            margin-bottom: 14px;
        }

        .message-top h3 {
            margin: 0 0 8px;
        }

        .message-top p {
            margin: 4px 0;
            color: #64748b;
            font-size: 14px;
        }

        .message-date {
            color: #64748b;
            font-size: 13px;
        }

        .message-subject {
            font-weight: bold;
            margin-bottom: 10px;
        }

        .message-body {
            line-height: 1.7;
            white-space: pre-wrap;
            color: #374151;
            margin-bottom: 18px;
        }

        .delete-form {
            display: flex;
            justify-content: flex-end;
            border-top: 1px solid #e5e7eb;
            padding-top: 14px;
        }

        .delete-button {
            background: #dc2626;
            color: white;
            border: none;
            padding: 10px 15px;
            border-radius: 8px;
            cursor: pointer;
            font-weight: bold;
        }

        .delete-button:hover {
            background: #991b1b;
        }

        .empty {
            background: white;
            padding: 35px;
            border-radius: 14px;
            text-align: center;
            color: #64748b;
        }

        @media (max-width: 600px) {
            main {
                margin: 20px auto;
                padding: 0 12px;
            }

            .message-card {
                padding: 16px;
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
        <h2>Contact Messages</h2>
        <p>View and manage messages submitted through the contact form.</p>
    </div>

    <% if (messages == null || messages.isEmpty()) { %>

        <div class="empty">
            <h3>No messages found</h3>
            <p>Contact form submissions will appear here.</p>
        </div>

    <% } else { %>

        <% for (Map<String, Object> message : messages) { %>

            <article class="message-card">

                <div class="message-top">
                    <div>
                        <h3><%= escapeHtml(message.get("name")) %></h3>

                        <p>
                            Email:
                            <%= escapeHtml(message.get("email")) %>
                        </p>

                        <p>
                            Message ID:
                            <%= escapeHtml(message.get("messageId")) %>
                        </p>
                    </div>

                    <div class="message-date">
                        <%= escapeHtml(message.get("createdAt")) %>
                    </div>
                </div>

                <div class="message-subject">
                    Subject:
                    <%= escapeHtml(message.get("subject")) %>
                </div>

                <div class="message-body"><%= escapeHtml(message.get("message")) %></div>

                <form class="delete-form"
                      method="post"
                      action="<%= request.getContextPath() %>/admin-delete-message"
                      onsubmit="return confirm('Are you sure you want to permanently delete this message?');">

                    <input type="hidden"
                           name="messageId"
                           value="<%= escapeHtml(message.get("messageId")) %>">

                    <button class="delete-button" type="submit">
                        Delete Message
                    </button>
                </form>

            </article>

        <% } %>

    <% } %>

</main>

</body>
</html>
```