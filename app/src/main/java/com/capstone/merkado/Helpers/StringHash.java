package com.capstone.merkado.Helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class StringHash {

    /**
     * Hashes the password.
     * @param password raw password.
     * @return hashed password.
     */
    public static String hashPassword(String password) {
        try {
            // Get a MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Hash the password
            byte[] hashedBytes = md.digest(password.getBytes());

            // Encode the hashed bytes to a string
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found!", e);
        }
    }

    /**
     * Encode a string to Base64.
     * @param input raw string.
     * @return encoded string
     */
    public static String encodeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        byte[] encodedBytes = Base64.getEncoder().encode(input.getBytes());
        return new String(encodedBytes);
    }

    /**
     * Decode a Base64 encoded string.
     * @param encoded raw string.
     * @return decoded string.
     */
    public static String decodeString(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return encoded;
        }
        byte[] decodedBytes = Base64.getDecoder().decode(encoded);
        return new String(decodedBytes);
    }
}
