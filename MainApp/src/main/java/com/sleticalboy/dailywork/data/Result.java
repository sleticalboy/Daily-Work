package com.sleticalboy.dailywork.data;

/**
 * Created on 20-3-31.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class Result<T> {

    public static class Success<R> extends Result<R> {}

    public static class Error extends Result<Throwable> {
        //
    }

    public static class Loading extends Result<Void> {
    }
}
