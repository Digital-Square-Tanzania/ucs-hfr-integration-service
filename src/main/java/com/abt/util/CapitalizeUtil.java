package com.abt.util;

/**
 * Utility to normalize casing while keeping short tokens uppercase.
 */
public class CapitalizeUtil {

    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitizedInput = input.replace("\"", "");
        String[] words = sanitizedInput.split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (word.length() <= 2) {
                    formatted.append(word.toUpperCase()).append(" ");
                } else {
                    String capWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
                    formatted.append(capWord).append(" ");
                }
            }
        }

        return formatted.toString().trim();
    }
}
