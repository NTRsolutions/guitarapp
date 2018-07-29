package com.demskigroup.guitaramps.Twiter_manager;

/**
 * @since  8/16/2017.
 * @author 3Embed.
 * @version 1.o.
 */

public class StringUtil
{
    public StringUtil() {
    }
    public static boolean isNullOrWhitespace(String string) {
        return string == null || string.isEmpty() || string.trim().isEmpty();
    }
}
