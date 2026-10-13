package com.rentinstead.servlet;

import com.rentinstead.dao.UserDAO;
import com.rentinstead.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/update-profile")
public class UpdateProfileServlet extends HttpServlet {

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

        String name = request.getParameter("name");
        String phone = request.getParameter("phone");

        if (name == null || name.trim().isEmpty()
                || phone == null
                || !phone.matches("[6-9][0-9]{9}")) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Enter a valid name and 10-digit Indian mobile number."
            );
            return;
        }

        name = name.trim();

        if (name.length() > 100) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Name must be at most 100 characters."
            );
            return;
        }

        try {
            long userId =
                    ((Number) session.getAttribute("userId")).longValue();

            User user = userDAO.findById(userId);

            if (user == null) {
                session.invalidate();
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "User account was not found."
                );
                return;
            }

            user.setName(name);
            user.setPhone(phone);

            boolean updated = userDAO.updateProfile(user);

            if (!updated) {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Profile could not be updated."
                );
                return;
            }

            session.setAttribute("userName", name);
            session.setAttribute("userPhone", phone);

            response.sendRedirect(
                    request.getContextPath() + "/html/dashboard/profile.html"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to update user profile.", e
            );
        }
    }
}