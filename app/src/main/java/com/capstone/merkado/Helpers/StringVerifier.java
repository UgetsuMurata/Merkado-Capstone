package com.capstone.merkado.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringVerifier {
    public static boolean isValidGmail(String email) {
        final String GMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";

        if (email == null || email.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(GMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
