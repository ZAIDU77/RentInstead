// ```java
package com.rentinstead.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private void setNoCacheHeaders(HttpServletResponse response) {
        response.setHeader(
                "Cache-Control",
                "no-store, no-cache, must-revalidate"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        setNoCacheHeaders(response);
        invalidateSession(request);

        response.sendRedirect(
                request.getContextPath() + "/index.html"
        );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        setNoCacheHeaders(response);
        invalidateSession(request);

        response.sendRedirect(
                request.getContextPath() + "/index.html"
        );
    }
}
