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

}
