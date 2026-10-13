package com.rentinstead.servlet;

import com.rentinstead.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/delete-account")
public class DeleteAccountServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Please log in first."
            );
            return;
        }

        String confirmation = request.getParameter("confirmation");

        if (!"DELETE".equals(confirmation)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Account deletion confirmation is required."
            );
            return;
        }

        try {
            long userId =
                    ((Number) session.getAttribute("userId")).longValue();

            boolean deactivated = userDAO.deactivateAccount(userId);

            if (!deactivated) {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Account could not be deactivated."
                );
                return;
            }

            session.invalidate();

            response.sendRedirect(
                    request.getContextPath()
                    + "/html/auth/login.html?accountDeleted=true"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to deactivate account.", e
            );
        }
    }
}