// ```java
package com.rentinstead.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/current-user")
public class CurrentUserServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("userId") == null
                || session.getAttribute("userName") == null) {

            response.getWriter().write("{\"loggedIn\":false}");
            return;
        }

        String firstName = String.valueOf(
                session.getAttribute("userName")
        );

        // Escape characters before placing the name in JSON.
        String safeName = firstName
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        response.getWriter().write(
                "{\"loggedIn\":true,\"firstName\":\""
                        + safeName + "\"}"
        );
    }
}
