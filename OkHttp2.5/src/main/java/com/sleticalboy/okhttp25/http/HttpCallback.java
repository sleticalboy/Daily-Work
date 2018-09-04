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
    Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public abstract void onSuccess(T response);

    public abstract void onFailure(Throwable e);
}
