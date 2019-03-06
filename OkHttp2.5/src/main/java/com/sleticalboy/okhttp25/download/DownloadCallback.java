package com.sleticalboy.okhttp25.download;

/**
 * Created on 18-9-3.
 * <p>
 * 回调都已经在 UI 线程了 不需要再做切换线程的操作
 *
 * @author leebin
 */
public interface DownloadCallback {

    /**
     * 下载出错时回调
     *
     * @param e {@link Throwable}
     */
    void onError(Throwable e);

    /**
     * 下载开始时回调
     *
     * @param total 下载内容的总长度
     */
    void onStart(long total);

    /**
     * 下载进度回调
     *
     * @param progress 进度条
     */
    void onProgress(float progress);

    /**
     * 下载暂停回调
     */
    void onPause();

    /**
     * 下载完成回调
     */
    void onComplete();

    /**
     * 取消下载回调
     */
    void onCancel();

    /**
     * 继续下载
     */
    void onResume();

    class SimpleCallback implements DownloadCallback {

        @Override
        public void onError(Throwable e) {
        }

        @Override
        public void onStart(long total) {
        }

        @Override
        public void onProgress(float progress) {
        }

        @Override
        public void onPause() {
        }

        @Override
        public void onComplete() {
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onResume() {
        }
    }
}
