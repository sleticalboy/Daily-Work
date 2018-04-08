package com.lee.wechatdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lee.wechatdemo.R;
import com.lee.wechatdemo.ShareManager;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        findViewById(R.id.btnShareTextWX).setOnClickListener(this);
        findViewById(R.id.btnShareImageWX).setOnClickListener(this);
        findViewById(R.id.btnShareUrlWX).setOnClickListener(this);
        findViewById(R.id.btnShareTextWB).setOnClickListener(this);
        findViewById(R.id.btnShareImageWB).setOnClickListener(this);
        findViewById(R.id.btnShareUrlWB).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnShareTextWX:
                ShareManager.newInstance().shareText2WeChat("text from " + getPackageName());
                break;
            case R.id.btnShareImageWX:
                break;
            case R.id.btnShareUrlWX:
                break;
            case R.id.btnShareTextWB:
                break;
            case R.id.btnShareImageWB:
                break;
            case R.id.btnShareUrlWB:
                break;
        }
    }
}
