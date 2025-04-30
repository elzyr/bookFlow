package com.bookflow.user;

import java.util.regex.Pattern;

public class PasswordCheck {
    private static String newPassword;

    public PasswordCheck(String oldPassword, String newPassword) {
        PasswordCheck.newPassword = newPassword;
    }

    public boolean checkPassword(String password) {
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
        return Pattern.matches(pattern, password);
    }

    public boolean isNewPasswordValid() {
        return checkPassword(newPassword);
    }

}
