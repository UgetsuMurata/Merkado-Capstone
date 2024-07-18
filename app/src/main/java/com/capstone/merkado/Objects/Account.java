package com.capstone.merkado.Objects;

/**
 * Stores the account information from Firebase.
 */
public class Account {
    String email, username;

    public Account() {
    }

    public Account(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
