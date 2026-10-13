package com.rentinstead.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 600_000;
    private static final int KEY_LENGTH = 256;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 128;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
        // Utility class.
    }

    public static String hashPassword(String password) {

        if (password == null
                || password.length() < MIN_PASSWORD_LENGTH
                || password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must contain 8 to 128 characters."
            );
        }

        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);

        char[] passwordChars = password.toCharArray();

        try {
            byte[] hash = deriveKey(
                    passwordChars,
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );

            return "pbkdf2-sha256$"
                    + ITERATIONS + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(hash);

        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    public static boolean verifyPassword(
            String password,
            String storedPasswordHash
    ) {

        if (password == null
                || storedPasswordHash == null
                || password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }

        try {
            String[] parts = storedPasswordHash.split("\\$", -1);

            if (parts.length != 4
                    || !"pbkdf2-sha256".equals(parts[0])) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            if (iterations < 100_000 || iterations > 2_000_000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);

            if (salt.length != SALT_LENGTH
                    || expectedHash.length != KEY_LENGTH / 8) {
                return false;
            }

            char[] passwordChars = password.toCharArray();

            byte[] actualHash;

            try {
                actualHash = deriveKey(
                        passwordChars,
                        salt,
                        iterations,
                        KEY_LENGTH
                );
            } finally {
                Arrays.fill(passwordChars, '\0');
            }

            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] deriveKey(
            char[] password,
            byte[] salt,
            int iterations,
            int keyLength
    ) {

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                keyLength
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Password hashing failed.", e
            );

        } finally {
            spec.clearPassword();
        }
    }
}
