package com.rentinstead.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentinstead.dao.UserDAO;
import com.rentinstead.model.User;
import com.rentinstead.util.TwilioSmsUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/otp/send")
public class OtpSendServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final UserDAO userDAO = new UserDAO();
    private final SecureRandom secureRandom = new SecureRandom();

    private static final long OTP_VALIDITY_MS = 5 * 60 * 1000L;

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

        String fullName = body.get("fullName") instanceof String
                ? ((String) body.get("fullName")).trim() : "";

        String phone = body.get("phone") instanceof String
                ? ((String) body.get("phone")).trim() : "";

        if (fullName.length() < 2 || fullName.length() > 100 ||
                !fullName.matches("[\\p{L} .'-]+")) {
            send(response, result, 400, "Please enter a valid full name.");
            return;
        }

        if (!phone.matches("[6-9][0-9]{9}")) {
            send(response, result, 400,
                    "Enter a valid 10-digit Indian mobile number.");
            return;
        }

        try {
            User existingUser = userDAO.findByPhone(phone);

            if (existingUser != null) {
                send(response, result, 409,
                        "This mobile number is already registered.");
                return;
            }
        } catch (SQLException e) {
            throw new ServletException(
                    "Could not check the phone number.", e);
        }

        HttpSession session = request.getSession(true);
        String previousPhone = (String) session.getAttribute("otpPhone");
        Long lastSent = (Long) session.getAttribute("otpLastSent");

        if (phone.equals(previousPhone) && lastSent != null &&
                System.currentTimeMillis() - lastSent < 60_000L) {
            send(response, result, 429,
                    "Please wait 60 seconds before requesting another OTP.");
            return;
        }

        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        try {
            TwilioSmsUtil.sendSms(
                    phone,
                    "Your RentInstead verification code is " + otp +
                            ". It expires in 5 minutes. Do not share this code."
            );

            session.setAttribute("otpPhone", phone);
            session.setAttribute("otpFullName", fullName);
            session.setAttribute("otpCode", otp);
            session.setAttribute("otpExpiry",
                    System.currentTimeMillis() + OTP_VALIDITY_MS);
            session.setAttribute("otpLastSent", System.currentTimeMillis());

            send(response, result, 200,
                    "OTP sent successfully. Please check your SMS.");
        } catch (Exception e) {
            getServletContext().log("Twilio OTP SMS delivery failed.", e);
            send(response, result, 502,
                    "SMS could not be sent. Check Twilio configuration and account status.");
        }
    }

    private void send(HttpServletResponse response,
                      Map<String, Object> result,
                      int status, String message) throws IOException {
        response.setStatus(status);
        result.put("message", message);
        mapper.writeValue(response.getWriter(), result);
    }
}
