package com.capstone.merkado.Helpers;

import com.capstone.merkado.Objects.VerificationCode;

import java.util.Locale;
import java.util.Random;

public class Generator {

    private static final String ALPHANUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random random = new Random();

    /**
     * Generates a 4-digit verification numerical code.
     * @param emailString raw email.
     * @return {@link com.capstone.merkado.Objects.VerificationCode} instance.
     */
    public static VerificationCode code(String emailString) {
        // Generate Code and save it as VerificationCode object.
        Random random = new Random();
        Integer codeInt = random.nextInt(10000);
        String code = String.format(Locale.getDefault(), "%04d", codeInt);
        return new VerificationCode(emailString, code);
    }

    public static String generateID() {
        long currentMillis = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();

        String millisString = Long.toString(currentMillis);
        for (int i = 0; i < 9; i++) {
            int index = (millisString.charAt(i % millisString.length()) + random.nextInt(ALPHANUMERIC.length())) % ALPHANUMERIC.length();
            sb.append(ALPHANUMERIC.charAt(index));

            if (i == 2 || i == 5) {
                sb.append('-'); // Insert dashes after every third character
            }
        }
        return sb.toString();
    }

    public static String generateKey() {
        long currentMillis = System.currentTimeMillis() % 100000;
        return String.format(Locale.getDefault(),"%05d", currentMillis);
    }

}
