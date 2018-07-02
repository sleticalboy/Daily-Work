package com.sleticalboy.scananimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author sleticalboy
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int DURATION = 5000;
    final static int ORIGINAL_HEIGHT = 322;
    private ImageView ivAnimHighBg;
    private ImageView ivAnimScanner;
    private TextView btnStop;
    private TextView btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        ivAnimHighBg = (ImageView) findViewById(R.id.iv_anim_high_bg);
        ivAnimScanner = (ImageView) findViewById(R.id.iv_anim_scanner);
        btnStop = (TextView) findViewById(R.id.btn_stop);
        btnStart = (TextView) findViewById(R.id.btn_start);
        initListener();
    }

    private void initListener() {
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        int height = ivAnimHighBg.getLayoutParams().height;
        int topMargin = ((RelativeLayout.LayoutParams) ivAnimScanner.getLayoutParams()).topMargin;
        if (id == R.id.btn_start) {
            for (int t = 0; t < DURATION; t++) {
                height = height * (t * ORIGINAL_HEIGHT / DURATION);
                topMargin = topMargin * (t * ORIGINAL_HEIGHT / DURATION);
            }
        } else if (id == R.id.btn_stop) {
        }
    }

    static class ViewWrapper {

        static final String HEIGHT = "height";
        static final String TOP_MARGIN = "topMargin";
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

        public int getTopMargin() {
            if (mView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                return ((RelativeLayout.LayoutParams) mView.getLayoutParams()).topMargin;
            }
            return 0;
        }

        public void setTopMargin(int topMargin) {
            if (mView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) mView.getLayoutParams()).topMargin -= topMargin;
            }
        }
    }
}
