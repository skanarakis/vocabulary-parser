package edu.teikav.robot.parser.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class TokenUtils
{
    private static Logger logger = LoggerFactory.getLogger(TokenUtils.class);
    private static Pattern digitsOnlyPattern = Pattern.compile("^([0-9]+\\.?[0-9]*|[0-9]*\\.[0-9]+)$");

    // Non-instantiable
    private TokenUtils() {}

    public static boolean isDegenerate(String token) {

        if (token.length() == 0) {
            logger.debug("Ignoring degenerate empty token");
            return true;
        }
        // 'I' is not a degenerate token
        if (token.length() == 1 && !token.equals("I")) {
            logger.debug("Ignoring degenerate token >>{}<<", token);
            return true;
        }

        return false;
    }

    public static boolean isDigitsOnly(String token) {
        if (digitsOnlyPattern.matcher(token).matches()) {
            logger.warn("Ignoring digits-only token >>{}<<", token);
            return true;
        }
        return false;
    }

    public static String removeNonLetterCharsInPrefix(String token) {

        int index = 0;
        char current = token.charAt(0);
        int length = token.length();
        while (index < length - 1 && (Character.isDigit(current) || current == '.' || current == ' ')) {
            index++;
            current = token.charAt(index);
        }
        if (index < length - 2) {
            return token.substring(index);
        } else {
            return token;
        }
    }
}
