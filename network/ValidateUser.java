package com.fourarc.videostatus.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUser {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String MobilePattern = "[0-9]{10}";
    public static boolean isNotNull(String txt) {
        return txt != null && txt.trim().length() > 0 ? true : false;
    }
    public static boolean isMax6(String txt) {
        return txt != null && txt.trim().length() > 5 ? true : false;
    }
    public static boolean isMax10(String txt) {
        return txt != null && txt.trim().length() > 9 ? true : false;
    }

    private static final String EMAIL_PATTERN ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static boolean validate(String email) {

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validateMobile(String email) {

        pattern = Pattern.compile(MobilePattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
