package edu.teikav.robot.parser.util;

import org.springframework.util.StringUtils;

public class GenericUtils {

    // Non-instantiable
    private GenericUtils() {}

    public static void validate(String input, String message) {
        if (StringUtils.isEmpty(input)) {
            throw new IllegalArgumentException(message);
        }
    }
}
