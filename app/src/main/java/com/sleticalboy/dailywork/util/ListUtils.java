package com.sleticalboy.dailywork.util;

import java.util.List;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class ListUtils {

    public static <E> void exchangeItem(List<E> source, int from, int to) {
        if (isEmpty(source)) {
            return;
        }
        E fromItem = source.get(from);
        E toItem = source.get(to);
        source.set(from, toItem);
        source.set(to, fromItem);
    }

    public static <E> boolean equals(List<E> actual, List<E> excepted) {
        if (actual == null) {
            return excepted == null;
        }
        if (excepted == null) {
            return false;
        }
        if (actual.size() != excepted.size()) {
            return false;
        }
        for (int i = 0; i < actual.size(); i++) {
            if (!ObjectUtils.equals(actual.get(i), excepted.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static <E> int getSize(List<E> source) {
        return isEmpty(source) ? 0 : source.size();
    }

    public static <E> boolean isEmpty(List<E> source) {
        return source == null || source.size() == 0;
    }

    public static <E> void relocation(List<E> source, int from, int to) {
        if (isEmpty(source)) {
            return;
        }
        E fromItem = source.remove(from);
        source.add(to, fromItem);
    }
}
