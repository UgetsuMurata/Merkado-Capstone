package com.capstone.merkado.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringVerifier {
    /**
     * Verifies if a gmail is valid. Valid (regex) format is: <em>^[a-zA-Z0-9._%+-]+@gmail\.com$</em>
     *
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

    /**
     * Verifies if the given username meets the specified criteria.
     *
     * @param username The username to verify.
     * @return A string indicating the validity or the reason for invalidity.
     */
    public static UsernameCode validateUsername(String username) {
        // Length check: 3 to 15 characters
        if (username.length() < 3 || username.length() > 15) {
            return UsernameCode.INVALID_LENGTH;
        }

        // Alphanumeric characters, underscores, and periods only
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            return UsernameCode.INVALID_CHARACTERS;
        }

        // No profanity (example, can be expanded as needed)
        String[] prohibitedWords = {"badword1", "badword2"}; // Add more offensive words as needed
        for (String word : prohibitedWords) {
            if (username.toLowerCase().contains(word)) {
                return UsernameCode.HAS_PROFANITY;
            }
        }

        // If all checks pass
        return UsernameCode.VALID;
    }

    public enum UsernameCode {
        INVALID_LENGTH, INVALID_CHARACTERS, HAS_PROFANITY, VALID
    }

    /**
     * Verifies if server code input is valid.
     * @param code raw string.
     * @return boolean value of validity.
     */
    public static Boolean isValidServerId(String code) {
        return code.matches("^[0-9A-Z]{3}-[0-9A-Z]{3}-[0-9A-Z]{3}$");
    }

    public static Boolean isValidServerKey(String code) {
        return code.matches("^[0-9]{5}$");
    }
}
