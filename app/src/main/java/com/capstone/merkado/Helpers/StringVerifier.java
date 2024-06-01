package com.capstone.merkado.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringVerifier {
    /**
     * Verifies if a gmail is valid. Valid (regex) format is: <em>^[a-zA-Z0-9._%+-]+@gmail\.com$</em>
     * @param email raw email.
     * @return Boolean value of email's validity.
     */
    public static boolean isValidGmail(String email) {
        final String GMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";

        if (email == null || email.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(GMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        // Check length
        if (password.length() < 8) {
            return false;
        }

        // Check for variety of character types
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}
