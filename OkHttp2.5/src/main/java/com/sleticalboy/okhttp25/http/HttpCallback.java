package com.sleticalboy.okhttp25.http;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public abstract class HttpCallback<T> implements Serializable {

    @SuppressWarnings("unchecked")
    final Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void onSuccess(T response);

    public abstract void onFailure(Throwable e);

    final boolean isPrimaryType() {
        final Class<T> tClass = getGenericClass();
        return tClass == int.class || tClass == Integer.class
                || tClass == short.class || tClass == Short.class
                || tClass == byte.class || tClass == Byte.class
                || tClass == char.class || tClass == Character.class
                || tClass == boolean.class || tClass == Boolean.class
                || tClass == float.class || tClass == Float.class
                || tClass == double.class || tClass == Double.class
                || tClass == long.class || tClass == Long.class;
    }

    final boolean isStringType() {
        return getGenericClass() == String.class;
    }

//    final boolean isOtherType() {
//        return !isStringType() && !isPrimaryType();
//    }
}
