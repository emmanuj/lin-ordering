package com.acs.clemson.ordering.util;

/**
 *
 * @author emmanuj
 */
public class StringParser {

    public static int toInt(String s, int beg, int end) {
        int num = s.charAt(0) - '0';
        int i = beg + 1;
        while (i <= end) {
            num = num * 10 + s.charAt(i) - '0';
            i += 1;
        }

        return num;
    }

    public static int toInt(String s) {
        return toInt(s, 0, s.length() - 1);
    }
}
