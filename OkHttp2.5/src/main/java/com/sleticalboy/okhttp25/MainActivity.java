package com.sleticalboy.okhttp25;

import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.io.FileSystem;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://www.baidu.com/")
                .get()
                .addHeader("custom-header", "minxing")
                .build();
        final Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                showError(e);
                if (!call.isCanceled()) {
                    call.cancel();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String string = response.body().string();
                    showMsg(string);
                }
                call.cancel();
            }
        });
    }

    private void showMsg(String msg) {
        toast(msg);
    }

    private void showError(Throwable e) {
        toast(e.getMessage());
    }

    private void toast(String msg) {
        tvResult.setText(msg);
        Looper.prepare();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
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
}
