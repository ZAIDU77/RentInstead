// ```java
package com.rentinstead.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public final class TwilioSmsUtil {

    private TwilioSmsUtil() {
        // Utility class
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required environment variable is missing: " + name
            );
        }

        return value.trim();
    }

    public static void sendSms(String indianPhone, String messageBody) {
        if (indianPhone == null || !indianPhone.matches("[6-9][0-9]{9}")) {
            throw new IllegalArgumentException(
                    "Enter a valid 10-digit Indian mobile number."
            );
        }

        if (messageBody == null || messageBody.isBlank()) {
            throw new IllegalArgumentException("SMS message cannot be empty.");
        }

        String accountSid = requiredEnv("TWILIO_ACCOUNT_SID");
        String authToken = requiredEnv("TWILIO_AUTH_TOKEN");
        String fromNumber = requiredEnv("TWILIO_FROM_NUMBER");

        if (!accountSid.startsWith("AC")) {
            throw new IllegalStateException("Twilio Account SID is invalid.");
        }

        if (!fromNumber.startsWith("+")) {
            throw new IllegalStateException(
                    "TWILIO_FROM_NUMBER must include the country code, e.g. +1..."
            );
        }

        Twilio.init(accountSid, authToken);

        Message.creator(
                new PhoneNumber("+91" + indianPhone),
                new PhoneNumber(fromNumber),
                messageBody
        ).create();
    }
}
