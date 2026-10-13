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

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in first."
            );
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (currentPassword == null || newPassword == null
                || confirmPassword == null) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "All password fields are required."
            );
            return;
        }

        if (newPassword.length() < 8 || newPassword.length() > 128) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "New password must contain 8 to 128 characters."
            );
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "New password and confirmation do not match."
            );
            return;
        }

        try {
            long userId =
                    ((Number) session.getAttribute("userId")).longValue();

            User user = userDAO.findById(userId);

            if (user == null || user.getPasswordHash() == null
                    || !PasswordUtil.verifyPassword(
                            currentPassword, user.getPasswordHash())) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Current password is incorrect."
                );
                return;
            }

            String newPasswordHash =
                    PasswordUtil.hashPassword(newPassword);

            boolean updated =
                    userDAO.updatePassword(userId, newPasswordHash);

            if (!updated) {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Password could not be changed."
                );
                return;
            }

            session.invalidate();

            response.sendRedirect(
                    request.getContextPath()
                    + "/html/auth/login.html?passwordChanged=true"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to change password.", e
            );
        }
    }
}