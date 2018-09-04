package com.sleticalboy.okhttp25.upload.custom;

/**
 * Created on 18-9-3.
 *
 * @author sleticalboy
 */
public interface ProgressCallback {

    /**
     * 开始下载之前回调
     *
     * @param contentLength 下载内容总长度
     */
    void onPreExecute(long contentLength);

    /**
     * 瞎子啊进度回调
     *
     * @param progress       下载进度
     * @param bytesTotalRead 已下载文件长度
     */
    void onProgress(int progress, long bytesTotalRead);

    class SimpleCallback implements ProgressCallback {
        @Override
        public void onPreExecute(long contentLength) {
        }

        @Override
        public void onProgress(int progress, long bytesTotalRead) {
        }
    }

}
