package com.sleticalboy.scananimation;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author sleticalboy
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int DURATION = 5000;
    private ImageView ivAnimLowBg;
    private ImageView ivAnimHighBg;
    private ImageView ivAnimScanner;
    private TextView btnStop;
    private TextView btnStart;
    private int mHeight;
    private int mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mHeight = ivAnimLowBg.getDrawable().getIntrinsicHeight();
        //final ClipDrawable drawable = (ClipDrawable) ivAnimHighBg.getDrawable();
    }


    private void initView() {
        ivAnimLowBg = findViewById(R.id.iv_anim_low_bg);
        ivAnimHighBg = (ImageView) findViewById(R.id.iv_anim_high_bg);
        ivAnimScanner = (ImageView) findViewById(R.id.iv_anim_scanner);
        btnStop = (TextView) findViewById(R.id.btn_stop);
        btnStart = (TextView) findViewById(R.id.btn_start);
        ivAnimLowBg.post(new Runnable() {
            @Override
            public void run() {
                mHeight = ivAnimLowBg.getHeight();
                mWidth = ivAnimHighBg.getWidth();
                Log.d("MainActivity", "mHeight:" + mHeight);
                Log.d("MainActivity", "mWidth:" + mWidth);
            }
        });
        mHeight = ivAnimHighBg.getDrawable().getIntrinsicHeight();
        mWidth = ivAnimLowBg.getDrawable().getIntrinsicWidth();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAnimHighBg.getLayoutParams();
        lp.height = mHeight;
        lp.width = mWidth;
        ivAnimHighBg.setLayoutParams(lp);

        initListener();
    }

    private void initListener() {
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btn_start) {
        } else if (id == R.id.btn_stop) {
        }
    }

    static class ViewWrapper {

        static final String HEIGHT = "height";
        final View mView;

        ViewWrapper(View view) {
            mView = view;
        }

        public int getHeight() {
            return mView.getLayoutParams().height;
        }

        public void setHeight(int height) {
            mView.getLayoutParams().height -= height;
        }
    }
}
