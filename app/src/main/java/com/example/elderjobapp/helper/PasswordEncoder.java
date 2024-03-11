package com.example.elderjobapp.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {
    public static String encodePassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        String hashedInputPassword = PasswordEncoder.encodePassword(inputPassword);
        return hashedInputPassword.equals(storedPassword);
    }
}
