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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("userId") instanceof Number)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please login to view your profile."
            );
            return;
        }

        long userId = ((Number) session.getAttribute("userId")).longValue();

        try {
            User user = userDAO.findById(userId);

            if (user == null) {
                session.invalidate();
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "User account not found."
                );
                return;
            }

            request.setAttribute("profileUser", user);
            request.getRequestDispatcher(
                    "/html/dashboard/profile.html"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Unable to load profile.", e);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("userId") instanceof Number)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please login to update your profile."
            );
            return;
        }

        String name = request.getParameter("fullName");
        String phone = request.getParameter("phone");

        if (name == null || name.trim().length() < 2
                || name.trim().length() > 80
                || phone == null
                || !phone.trim().matches("[6-9][0-9]{9}")) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Enter a valid name and 10-digit mobile number."
            );
            return;
        }

        long userId = ((Number) session.getAttribute("userId")).longValue();

        try {
            User user = userDAO.findById(userId);

            if (user == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "User account not found."
                );
                return;
            }

            user.setFullName(name.trim());
            user.setPhone(phone.trim());

            if (userDAO.updateProfile(user)) {
                session.setAttribute("userName", user.getFullName());
                response.sendRedirect(
                        request.getContextPath() + "/profile?updated=true"
                );
            } else {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Profile was not updated. Please try again."
                );
            }

        } catch (SQLException e) {
            throw new ServletException("Unable to update profile.", e);
        }
    }
}