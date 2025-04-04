package com.tp.opencourse.utils;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }
    public static boolean isNullOrEmpty(String field) {
        return field == null || field.isEmpty();
    }

}
