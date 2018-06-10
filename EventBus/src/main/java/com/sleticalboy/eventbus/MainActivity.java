package com.sleticalboy.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sleticalboy.eventbus.event.EventMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author sleticalboy
 */
public class MainActivity extends AppCompatActivity {

    private TextView tvChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tvChange = (TextView) findViewById(R.id.tv_change);
        Button btnJump = (Button) findViewById(R.id.btn_jump);
        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PostEventActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // org.greenrobot.eventbus.EventBusException: Subscriber
    // class com.sleticalboy.eventbus.MainActivity and its super classes
    // have no public methods with the @Subscribe annotation
    // 此处的方法修饰符必须是 public 否则会报以上错误
    // ui 线程, 更新 ui， 不能做耗时操作，避免阻塞 ui
    // 使用该模式的订阅者方法必须快速返回，以避免阻塞主线程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMain(EventMessage msg) {
        tvChange.setText(msg.mMessage);
        Log.d("MainActivity", msg.mMessage);
        Log.d("MainActivity", "onEventMain = " + Thread.currentThread().getName());
    }

    // 和 post 事件发生在同一线程，默认线程
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventPosting(EventMessage msg) {
        Log.d("MainActivity", msg.mMessage);
        Log.d("MainActivity", "onEventPosting = " + Thread.currentThread().getName());
    }

    // worker 线程
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBackground(EventMessage msg) {
        Log.d("MainActivity", msg.mMessage);
        Log.d("MainActivity", "onEventBackground = " + Thread.currentThread().getName());
    }

    // ui 线程
    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onEventMainOrdered(EventMessage msg) {
        Log.d("MainActivity", msg.mMessage);
        Log.d("MainActivity", "onEventMainOrdered = " + Thread.currentThread().getName());
    }

    // worker 线程
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEventAsync(EventMessage msg) {
        Log.d("MainActivity", msg.mMessage);
        Log.d("MainActivity", "onEventAsync = " + Thread.currentThread().getName());
    }
}
