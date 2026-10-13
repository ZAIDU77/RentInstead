package com.rentinstead.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/otp/verify")
public class OtpVerifyServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        if (request.getContentType() == null ||
                !request.getContentType().toLowerCase()
                        .startsWith("application/json")) {
            send(response, result, 415,
                    "Content-Type must be application/json.");
            return;
        }

        Map<?, ?> body;
        try {
            body = mapper.readValue(request.getInputStream(), Map.class);
        } catch (Exception e) {
            send(response, result, 400, "Invalid request data.");
            return;
        }

        String phone = body.get("phone") instanceof String
                ? ((String) body.get("phone")).trim() : "";
        String otp = body.get("otp") instanceof String
                ? ((String) body.get("otp")).trim() : "";
        String fullName = body.get("fullName") instanceof String
                ? ((String) body.get("fullName")).trim() : "";
        String password = body.get("password") instanceof String
                ? (String) body.get("password") : "";

        if (!phone.matches("[6-9][0-9]{9}") ||
                !otp.matches("[0-9]{6}")) {
            send(response, result, 400,
                    "Enter a valid phone number and 6-digit OTP.");
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null ||
                !phone.equals(session.getAttribute("otpPhone"))) {
            send(response, result, 400,
                    "Please request an OTP for this phone number first.");
            return;
        }

        String savedOtp = (String) session.getAttribute("otpCode");
        Object expiryValue = session.getAttribute("otpExpiry");

        if (savedOtp == null || !(expiryValue instanceof Long) ||
                System.currentTimeMillis() > (Long) expiryValue) {
            clearOtp(session);
            send(response, result, 400,
                    "OTP expired. Please request a new OTP.");
            return;
        }

        if (!savedOtp.equals(otp)) {
            send(response, result, 400, "Incorrect OTP.");
            return;
        }

        String savedName = (String) session.getAttribute("otpFullName");

        if (savedName == null || !savedName.equals(fullName)) {
            send(response, result, 400,
                    "Full name does not match the OTP request.");
            return;
        }

        if (password.length() < 8 || password.length() > 128) {
            send(response, result, 400,
                    "Password must contain 8 to 128 characters.");
            return;
        }

        try {
            if (userDAO.findByPhone(phone) != null) {
                clearOtp(session);
                send(response, result, 409,
                        "This mobile number is already registered.");
                return;
            }

            String passwordHash = PasswordUtil.hashPassword(password);
            User user = new User(fullName, phone, passwordHash);

            if (!userDAO.registerUser(user)) {
                send(response, result, 500,
                        "Registration could not be completed.");
                return;
            }

            clearOtp(session);
            request.changeSessionId();

            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getFullName());
            session.setAttribute("userRole", user.getRole());
            session.setMaxInactiveInterval(30 * 60);

            result.put("success", true);
            result.put("message", "Phone verified and account created successfully.");
            result.put("redirect", request.getContextPath() + "/index.html");
            response.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(response.getWriter(), result);

        } catch (SQLException e) {
            throw new ServletException(
                    "Database error during registration.", e);
        }
    }

    private void clearOtp(HttpSession session) {
        session.removeAttribute("otpPhone");
        session.removeAttribute("otpFullName");
        session.removeAttribute("otpCode");
        session.removeAttribute("otpExpiry");
        session.removeAttribute("otpLastSent");
    }

    private void send(HttpServletResponse response,
                      Map<String, Object> result,
                      int status, String message) throws IOException {
        response.setStatus(status);
        result.put("message", message);
        mapper.writeValue(response.getWriter(), result);
    }
}
