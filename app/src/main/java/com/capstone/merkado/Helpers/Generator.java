package com.capstone.merkado.Helpers;

import com.capstone.merkado.Objects.VerificationCode;

import java.util.Locale;
import java.util.Random;

public class Generator {

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

}
