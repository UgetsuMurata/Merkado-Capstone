package com.capstone.merkado.Objects;

/**
 * Contains the verification code and the time it was generated.
 */
public class VerificationCode {
    String email, code;
    Long generationTime;

    public VerificationCode(String email, String code) {
        this.email = email;
        this.code = code;
        this.generationTime = System.currentTimeMillis();
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }

    /**
     * Checks if the code is still valid (within 2 minutes).
     *
     * @return Boolean value of whether or not the code is valid.
     */
    public Boolean isCodeStillValid() {
        return System.currentTimeMillis() - generationTime < 120000; // 2-minute valid timeframe.
    }

    /**
     * Calculates the remaining time.
     *
     * @return remaining time.
     */
    public Long getRemainingTime() {
        return 120000 - System.currentTimeMillis() - generationTime;
    }
}
