package com.sleticalboy.dailywork.util;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class ObjectUtils {

    public static boolean equals(Object actual, Object excepted) {
        return actual == excepted || (actual == null ? excepted == null : actual.equals(excepted));
    }
}
