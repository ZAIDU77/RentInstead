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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        if (phone == null || password == null
                || !phone.trim().matches("[6-9][0-9]{9}")
                || password.isEmpty()
                || password.length() > 128) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Please enter a valid mobile number and password."
            );
            return;
        }

        try {
            User user = userDAO.findByPhone(phone.trim());

            if (user == null
                    || !"ACTIVE".equals(user.getAccountStatus())
                    || user.getPasswordHash() == null
                    || !PasswordUtil.verifyPassword(
                            password, user.getPasswordHash())) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid mobile number or password."
                );
                return;
            }

            // Invalidate the old session after successful authentication.
            HttpSession oldSession = request.getSession(false);

            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create a new authenticated session.
            HttpSession session = request.getSession(true);

            String fullName = user.getFullName().trim();
            String firstName = fullName.split("\\s+")[0];

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", firstName);
            session.setAttribute("userRole", user.getRole());

            session.setMaxInactiveInterval(30 * 60);

            // Redirect after successful login.
            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect(
                        request.getContextPath() + "/admin-dashboard"
                );
            } else {
                response.sendRedirect(
                        request.getContextPath() + "/index.html"
                );
            }

        } catch (SQLException e) {
            throw new ServletException(
                    "A database error occurred during login.", e
            );
        }
    }
}
