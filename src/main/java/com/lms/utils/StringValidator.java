package com.lms.utils;

public class StringValidator {
    public static void validateString(String value, String message) {
        if(value == null || value.trim().isEmpty()){
            throw new IllegalArgumentException(message);
        }
    }
}
