package com.rentinstead.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/logout",
        "/html/dashboard/*",
        "/html/rentals/*",
        "/html/items/add-item.html",
        "/add-item",
        "/my-rentals",
        "/book-item",
        "/update-profile",
        "/change-password",
        "/delete-account",
        "/delete-item",
        "/edit-item",
        "/admin-dashboard",
        "/admin-users",
        "/admin-bookings",
        "/admin-messages",
        "/admin-reports",
        "/admin-export-reports",
        "/admin-categories",
        "/admin-category-items",
        "/admin-update-category",
        "/admin-delete-category"
})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization required.
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        httpResponse.setHeader("Cache-Control", "no-store");
        httpResponse.setHeader("Pragma", "no-cache");

        HttpSession session = httpRequest.getSession(false);

        boolean loggedIn = session != null
                && session.getAttribute("userId") != null;

        String requestUri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        String path = requestUri.substring(contextPath.length());

        boolean adminRoute = path.startsWith("/admin-");

        if (!loggedIn) {
            if (adminRoute) {
                httpResponse.sendRedirect(
                        contextPath + "/html/auth/login.html"
                );
            } else if (path.startsWith("/html/")) {
                httpResponse.sendRedirect(
                        contextPath + "/html/auth/login.html"
                );
            } else {
                httpResponse.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Please log in to access this resource."
                );
            }
            return;
        }

        if (adminRoute
                && !"ADMIN".equals(session.getAttribute("userRole"))) {

            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Admin access required."
            );
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // No resources to release.
    }
}
