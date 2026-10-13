<!-- ```jsp -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.rentinstead.model.User" %>
<%
    User profileUser = (User) request.getAttribute("profileUser");

    String fullName = "";
    String phone = "";

    if (profileUser != null) {
        fullName = profileUser.getFullName() == null ? "" : profileUser.getFullName();
        phone = profileUser.getPhone() == null ? "" : profileUser.getPhone();
    }

    String safeName = fullName
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");

    String safePhone = phone
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile | RentInstead</title>

    <base href="<%= request.getContextPath() %>/">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:wght@400;500;600;700;800&family=DM+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: 'DM Sans', sans-serif;
            background: #f5f7fb;
            color: #172554;
        }

        .page-header {
            padding: 35px 0;
            background: #eef2ff;
            border-bottom: 1px solid #e2e8f0;
        }

        .page-header h1 {
            font-family: 'Bricolage Grotesque', sans-serif;
            font-size: clamp(28px, 4vw, 38px);
            font-weight: 800;
        }

        .page-header p {
            margin: 8px 0 0;
            color: #64748b;
        }

        .profile-page {
            padding: 32px 0 60px;
        }

        .profile-layout {
            display: grid;
            grid-template-columns: 280px minmax(0, 1fr);
            align-items: start;
            gap: 24px;
        }

        .profile-sidebar,
        .profile-card {
            overflow: hidden;
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            background: white;
        }

        .profile-sidebar {
            padding: 25px 20px;
            text-align: center;
        }

        .avatar {
            display: grid;
            place-items: center;
            width: 88px;
            height: 88px;
            margin: 0 auto 15px;
            border: 4px solid #dbeafe;
            border-radius: 50%;
            background: #1d4ed8;
            color: white;
            font-size: 32px;
            font-weight: 800;
        }

        .profile-sidebar h2 {
            margin-bottom: 5px;
            font-size: 20px;
            font-weight: 800;
            overflow-wrap: anywhere;
        }

        .profile-sidebar p {
            margin-bottom: 15px;
            color: #64748b;
            font-size: 13px;
            overflow-wrap: anywhere;
        }

        .member-tag {
            display: inline-block;
            padding: 6px 11px;
            border-radius: 20px;
            background: #dbeafe;
            color: #1d4ed8;
            font-size: 11px;
            font-weight: 700;
        }

        .sidebar-links {
            display: grid;
            gap: 5px;
            margin-top: 25px;
            text-align: left;
        }

        .sidebar-links a {
            display: flex;
            align-items: center;
            gap: 11px;
            padding: 12px;
            border-radius: 9px;
            color: #475569;
            font-size: 13px;
            font-weight: 600;
            text-decoration: none;
        }

        .sidebar-links a:hover,
        .sidebar-links a.active {
            background: #eff6ff;
            color: #1d4ed8;
        }

        .profile-card {
            margin-bottom: 20px;
        }

        .card-heading {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            padding: 22px 25px;
            border-bottom: 1px solid #e2e8f0;
        }

        .card-heading h2 {
            margin: 0;
            font-size: 18px;
            font-weight: 800;
        }

        .card-body {
            padding: 25px;
        }

        .form-label {
            margin-bottom: 7px;
            font-size: 13px;
            font-weight: 700;
        }

        .form-control,
        .form-select {
            min-height: 46px;
            border-color: #cbd5e1;
            border-radius: 9px;
            font-size: 14px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: #1d4ed8;
            box-shadow: 0 0 0 3px #1d4ed81a;
        }

        .primary-btn {
            padding: 11px 18px;
            border: 0;
            border-radius: 9px;
            background: #1d4ed8;
            color: white;
            font-size: 13px;
            font-weight: 700;
        }

        .primary-btn:hover {
            background: #1e40af;
        }

        .secondary-btn {
            padding: 10px 16px;
            border: 1px solid #cbd5e1;
            border-radius: 9px;
            background: white;
            color: #334155;
            font-size: 13px;
            font-weight: 700;
        }

        .help-text {
            margin-top: 7px;
            color: #64748b;
            font-size: 12px;
        }

        .message {
            margin: 20px 25px 0;
            padding: 12px;
            border-radius: 9px;
            font-size: 13px;
        }

        .account-row {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 15px;
            padding: 17px 25px;
            border-bottom: 1px solid #e2e8f0;
        }

        .account-row:last-child {
            border-bottom: 0;
        }

        .account-row strong {
            display: block;
            font-size: 13px;
        }

        .account-row small {
            display: block;
            margin-top: 4px;
            color: #64748b;
            font-size: 12px;
        }

        footer {
            padding: 20px;
            border-top: 1px solid #e2e8f0;
            background: white;
            color: #64748b;
            font-size: 12px;
            text-align: center;
        }

        @media (max-width: 800px) {
            .profile-layout {
                grid-template-columns: 1fr;
            }

            .profile-sidebar {
                padding: 20px;
            }

            .sidebar-links {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 480px) {
            .card-heading,
            .card-body {
                padding: 18px;
            }

            .account-row {
                padding: 16px 18px;
            }

            .sidebar-links {
                grid-template-columns: 1fr;
            }
        }
    </style>
    <link rel="icon" type="image/svg+xml" href="/RentInstead/favicon.svg">
</head>

<body>
    <header id="siteNavbar"></header>

    <section class="page-header">
        <div class="container">
            <h1>My Profile</h1>
            <p>Manage your personal information and account settings.</p>
        </div>
    </section>

    <main class="profile-page">
        <div class="container">
            <div class="profile-layout">

                <aside class="profile-sidebar">
                    <div class="avatar" id="profileAvatar">U</div>
                    <h2 id="sidebarName">Your Name</h2>
                    <p id="sidebarPhone">Phone number not added</p>

                    <span class="member-tag">
                        <i class="bi bi-person-check"></i> RentInstead Member
                    </span>

                    <nav class="sidebar-links">
                        <a href="#personalInfo" class="active">
                            <i class="bi bi-person"></i> Personal information
                        </a>
                        <a href="html/rentals/my-rentals.html">
                            <i class="bi bi-bag"></i> My rentals
                        </a>
                        <a href="html/dashboard/wishlist.html">
                            <i class="bi bi-heart"></i> Wishlist
                        </a>
                        <a href="html/dashboard/notifications.html">
                            <i class="bi bi-bell"></i> Notifications
                        </a>
                    </nav>
                </aside>

                <div>
                    <section class="profile-card" id="personalInfo">
                        <div class="card-heading">
                            <h2>Personal information</h2>
                            <i class="bi bi-person-vcard text-primary fs-4"></i>
                        </div>

                        <div id="profileMessage" class="message"
                             role="status" aria-live="polite" hidden></div>

                        <div class="card-body">
                            <form id="profileForm"
                                  action="<%= request.getContextPath() %>/profile"
                                  method="post">

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="fullName" class="form-label">
                                            Full name *
                                        </label>
                                        <input type="text"
                                               class="form-control"
                                               id="fullName"
                                               name="fullName"
                                               value="<%= safeName %>"
                                               minlength="2"
                                               maxlength="80"
                                               placeholder="Enter your full name"
                                               required>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="phone" class="form-label">
                                            Phone number *
                                        </label>
                                        <input type="tel"
                                               class="form-control"
                                               id="phone"
                                               name="phone"
                                               value="<%= safePhone %>"
                                               placeholder="10-digit mobile number"
                                               maxlength="10"
                                               pattern="[6-9][0-9]{9}"
                                               title="Enter a valid 10-digit Indian mobile number"
                                               required>
                                        <div class="help-text">
                                            Enter your 10-digit mobile number.
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="email" class="form-label">
                                            Email address
                                        </label>
                                        <input type="email"
                                               class="form-control"
                                               id="email"
                                               maxlength="120"
                                               placeholder="you@example.com">
                                    </div>

                                    <div class="col-md-6">
                                        <label for="college" class="form-label">
                                            College / University
                                        </label>
                                        <input type="text"
                                               class="form-control"
                                               id="college"
                                               maxlength="150"
                                               placeholder="Your college name">
                                    </div>

                                    <div class="col-md-6">
                                        <label for="course" class="form-label">
                                            Course
                                        </label>
                                        <select id="course" class="form-select">
                                            <option value="">Select course</option>
                                            <option>BCA</option>
                                            <option>B.Tech</option>
                                            <option>B.Sc</option>
                                            <option>B.Com</option>
                                            <option>BA</option>
                                            <option>MCA</option>
                                            <option>MBA</option>
                                            <option>Other</option>
                                        </select>
                                    </div>

                                    <div class="col-md-6">
                                        <label for="year" class="form-label">
                                            Study year
                                        </label>
                                        <select id="year" class="form-select">
                                            <option value="">Select year</option>
                                            <option>1st Year</option>
                                            <option>2nd Year</option>
                                            <option>3rd Year</option>
                                            <option>4th Year</option>
                                            <option>Postgraduate</option>
                                        </select>
                                    </div>

                                    <div class="col-12">
                                        <label for="location" class="form-label">
                                            City / Campus area
                                        </label>
                                        <input type="text"
                                               class="form-control"
                                               id="location"
                                               maxlength="150"
                                               placeholder="City or campus area">
                                    </div>
                                </div>

                                <div class="d-flex flex-wrap gap-2 mt-4">
                                    <button type="submit" class="primary-btn">
                                        <i class="bi bi-check2-circle me-1"></i>
                                        Save changes
                                    </button>
                                    <button type="reset" class="secondary-btn">
                                        Reset form
                                    </button>
                                </div>
                            </form>
                        </div>
                    </section>

                    <section class="profile-card">
                        <div class="card-heading">
                            <h2>Account information</h2>
                            <i class="bi bi-shield-check text-primary fs-4"></i>
                        </div>

                        <div class="account-row">
                            <div>
                                <strong>Phone verification</strong>
                                <small>
                                    Verification status is managed by the server.
                                </small>
                            </div>
                            <span class="badge text-bg-secondary">Not verified</span>
                        </div>

                        <div class="account-row">
                            <div>
                                <strong>Password &amp; security</strong>
                                <small>
                                    Manage your account credentials securely.
                                </small>
                            </div>
                            <a href="html/auth/forgot-password.html"
                               class="text-primary small fw-bold text-decoration-none">
                                Reset password
                            </a>
                        </div>
                    </section>
                </div>

            </div>
        </div>
    </main>

    <footer>&copy; 2026 RentInstead. Access instead of ownership.</footer>

    <script src="js/navbar.js"></script>

    <script>
        const profileForm = document.getElementById("profileForm");
        const profileMessage = document.getElementById("profileMessage");
        const fullNameInput = document.getElementById("fullName");
        const phoneInput = document.getElementById("phone");

        function updateProfilePreview() {
            const name = fullNameInput.value.trim() || "Your Name";
            const phone = phoneInput.value.trim();

            document.getElementById("sidebarName").textContent = name;
            document.getElementById("profileAvatar").textContent =
                name === "Your Name" ? "U" : name.charAt(0).toUpperCase();

            document.getElementById("sidebarPhone").textContent =
                phone ? "+91 " + phone : "Phone number not added";
        }

        function showMessage(message, type) {
            profileMessage.hidden = false;
            profileMessage.className = "message alert alert-" + type;
            profileMessage.textContent = message;
        }

        fullNameInput.addEventListener("input", updateProfilePreview);
        phoneInput.addEventListener("input", updateProfilePreview);

        profileForm.addEventListener("reset", function () {
            setTimeout(function () {
                updateProfilePreview();
            }, 0);
        });

        const params = new URLSearchParams(window.location.search);

        if (params.get("updated") === "true") {
            showMessage("Profile updated successfully!", "success");
        }

        updateProfilePreview();
    </script>
</body>
</html>
