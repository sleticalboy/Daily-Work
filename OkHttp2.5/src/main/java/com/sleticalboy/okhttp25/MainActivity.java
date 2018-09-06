package com.sleticalboy.okhttp25;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sleticalboy.okhttp25.download.DownloadCallback;
import com.sleticalboy.okhttp25.download.OkDownloader;
import com.sleticalboy.okhttp25.http.HttpCallback;
import com.sleticalboy.okhttp25.http.HttpClient;
import com.sleticalboy.okhttp25.http.builder.DeleteBuilder;
import com.sleticalboy.okhttp25.http.builder.GetBuilder;
import com.sleticalboy.okhttp25.http.builder.PostBuilder;
import com.sleticalboy.okhttp25.http.builder.PutBuilder;
import com.sleticalboy.okhttp25.http.builder.RequestBuilder;
import com.squareup.okhttp.internal.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;

public final class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tvResult;
    private OkDownloader mDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        getTest();

        initDownload();
    }

    private void getTest() {
        final RequestBuilder get = new GetBuilder()
                .url("http://www.baidu.com/")
                .header("custom-header", "minxing");
        HttpClient.getInstance().asyncExecute(get, new HttpCallback<String>() {

            @Override
            public void onSuccess(String response) {
                showMsg(response);
            }

            @Override
            public void onFailure(Throwable e) {
                showError(e);
            }
        });
    }

    private void postTest() {
        RequestBuilder postBuilder = new PostBuilder()
                .url("")
                .post(new HashMap<>(), null);
        HttpClient.getInstance().asyncExecute(postBuilder, null);
    }

    private void putTest() {
        RequestBuilder putBuilder = new PutBuilder()
                .url("")
                .put(new HashMap<>(), null);
        HttpClient.getInstance().asyncExecute(putBuilder, null);
    }

    private void deleteTest() {
        RequestBuilder deleteBuilder = new DeleteBuilder()
                .url("")
                .delete(null);
        HttpClient.getInstance().asyncExecute(deleteBuilder, null);
    }

    private void initDownload() {
        final File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        final String url = "http://gdown.baidu.com/data/wisegame/df65a597122796a4/weixin_821.apk";
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        File apk = new File(file, fileName);

        NumberFormat format = NumberFormat.getInstance();
        mDownloader = new OkDownloader(url, apk, true);
        mDownloader.setDownloadCallback(new DownloadCallback.SimpleCallback() {
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError() " + e.getMessage(), e);
                Log.d(TAG, "onError() delete file " + apk.delete());
            }

            @Override
            public void onStart(long total) {
                Log.d(TAG, "onStart() called with: total = [" + total + "]");
                if (apk.exists()) {
                    Log.d(TAG, "initDownload file exist, delete it");
                    boolean delete = apk.delete();
                    Log.d(TAG, "initDownload delete file: " + delete);
                }
            }

            @Override
            public void onProgress(float progress) {
                Log.d(TAG, "onProgress() called with: progress = [" + format.format(progress) + "]");
            }

            @Override
            public void onPause() {
                Log.d(TAG, "onPause() called");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete() saved file to " + apk.getAbsolutePath());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel() delete file " + apk.delete());
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
        toast(e == null ? "" : e.getMessage());
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
        mDownloader.resume();
    }

    public void cancelDownload(View view) {
        mDownloader.cancel();
    }
}
