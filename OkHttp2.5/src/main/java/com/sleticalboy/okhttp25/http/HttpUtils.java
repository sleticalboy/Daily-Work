package com.sleticalboy.okhttp25.http;

public final class HttpUtils {

    private HttpUtils() {
        throw new RuntimeException();
    }

    /**
     * 转换成进度
     *
     * @param read  已下载
     * @param total 总长度
     * @return 进度
     */
    public static int toProgress(long read, long total) {
        return (int) (100F * read / total);
    }
}
