package com.sleticalboy.sample.jnidemo;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public final class JniDemo extends Activity {

    private static final String TAG = "JniDemo";

    static {
        System.loadLibrary("jni_demo");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jni_demo);
        findViewById(R.id.btn_native_call_java).setOnClickListener(v -> {
            nCallJava(this);
        });
        TextView textHolder = findViewById(R.id.tv_text_holder);
        findViewById(R.id.btn_java_call_native).setOnClickListener(v -> {
            textHolder.setText(nGetString());
        });
    }

    private static native void nCallJava(JniDemo context);

    private static native String nGetString();

    // called by jni
    private static void sayHello(Context context, String hello) {
        Log.d(TAG, "sayHello() called with: hello = [" + hello + "]");
        Toast.makeText(context, hello, Toast.LENGTH_SHORT).show();
    }
}