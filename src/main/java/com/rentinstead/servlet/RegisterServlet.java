// ```java
package com.rentinstead.servlet;

import com.rentinstead.dao.UserDAO;
import com.rentinstead.model.User;
import com.rentinstead.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        // Check required fields.
        if (fullName == null || phone == null
                || password == null || confirmPassword == null) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Required registration fields are missing."
            );
            return;
        }

        fullName = fullName.trim();
        phone = phone.trim();

        // Validate full name.
        if (fullName.length() < 2
                || fullName.length() > 100
                || !fullName.matches("[\\p{L} .'-]+")) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Please enter a valid full name."
            );
            return;
        }

        // Validate Indian mobile number.
        if (!phone.matches("[6-9][0-9]{9}")) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Enter a valid 10-digit Indian mobile number."
            );
            return;
        }

        // Validate password length.
        if (password.length() < 8 || password.length() > 128) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Password must contain 8 to 128 characters."
            );
            return;
        }

        // Confirm password.
        if (!password.equals(confirmPassword)) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Passwords do not match."
            );
            return;
        }

        try {
            // Check whether this phone number is already registered.
            if (userDAO.findByPhone(phone) != null) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/html/auth/register.html?error=exists"
                );
                return;
            }

            // Hash the password before saving it.
            String passwordHash = PasswordUtil.hashPassword(password);

            User user = new User(
                    fullName,
                    phone,
                    passwordHash
            );

            // Save the new user in the database.
            if (!userDAO.registerUser(user)) {

                response.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Registration could not be completed."
                );
                return;
            }

            // Clear any existing session.
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create a new session after successful registration.
            HttpSession session = request.getSession(true);

            String firstName = user.getFullName()
                    .trim()
                    .split("\\s+")[0];

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", firstName);
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(30 * 60);

            // Redirect to the home page.
            response.sendRedirect(
                    request.getContextPath() + "/index.html"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "A database error occurred during registration.",
                    e
            );
        }
    }
}
