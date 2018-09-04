package com.sleticalboy.okhttp25;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sleticalboy.okhttp25.download.DownloadCallback;
import com.sleticalboy.okhttp25.download.ProgressDownloader;
import com.sleticalboy.okhttp25.http.HttpCallback;
import com.sleticalboy.okhttp25.http.HttpClient;
import com.sleticalboy.okhttp25.http.builder.AbstractBuilder;
import com.sleticalboy.okhttp25.http.builder.GetBuilder;
import com.squareup.okhttp.internal.io.FileSystem;

import java.io.File;
import java.io.IOException;

public final class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tvResult;
    private long lastPoint = 0L;
    private ProgressDownloader mDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        final AbstractBuilder builder = new GetBuilder().url("http://www.baidu.com/")
                .header("custom-header", "minxing");
        HttpClient.getInstance().asyncExecute(builder, new HttpCallback<String>() {

            @Override
            public void onSuccess(String response) {
                showMsg(response);
            }

            @Override
            public void onFailure(Throwable e) {
                showError(e);
            }
        });

        initDownload();
    }

    private void initDownload() {
        final File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        final String url = "http://gdown.baidu.com/data/wisegame/df65a597122796a4/weixin_821.apk";
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        File apk = new File(file, fileName);
        mDownloader = new ProgressDownloader(url, apk);
        mDownloader.setDownloadCallback(new DownloadCallback.SimpleCallback() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage(), e);
            }

            @Override
            public void onStart(long total) {
                Log.d(TAG, "onStart() called with: total = [" + total + "]");
            }

            @Override
            public void onProgress(int progress, long bytesTotalRead) {
                lastPoint = bytesTotalRead;
                Log.d(TAG, "onProgress() called with: progress = [" + progress + "], bytesTotalRead = [" + bytesTotalRead + "]");
            }

            @Override
            public void onPause() {
                Log.d(TAG, "onPause() called");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete() called");
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel() called");
            }

            @Override
            public void onResume() {
                Log.d(TAG, "onResume() called");
            }
        });
    }

    private void showMsg(String msg) {
        toast(msg);
    }

    private void toast(String msg) {
        tvResult.append(msg);
        tvResult.append(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showError(Throwable e) {
        toast(e.getMessage());
    }

    private void deleteContents(File directory) throws IOException {
        FileSystem.SYSTEM.deleteContents(directory);
    }

    private void rename(File from, File to) throws IOException {
        FileSystem.SYSTEM.rename(from, to);
    }

    private void deleteFile(File file) throws IOException {
        FileSystem.SYSTEM.delete(file);
    }

    public void startDownload(View view) {
        mDownloader.start();
    }

    public void pauseDownload(View view) {
        mDownloader.pause();
    }

    public void resumeDownload(View view) {
        mDownloader.resume(lastPoint);
    }

    public void cancelDownload(View view) {
        mDownloader.cancel();
    }
}
