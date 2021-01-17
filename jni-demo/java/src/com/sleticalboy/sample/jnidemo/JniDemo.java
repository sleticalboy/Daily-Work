package com.sleticalboy.sample.jnidemo;

import android.annotation.Nullable;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public final class JniDemo extends Activity {

    static {
        System.loadLibrary("jni_demo");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jni_demo);
        sayHello(this);
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }

    private static native void sayHello(Context context);
}