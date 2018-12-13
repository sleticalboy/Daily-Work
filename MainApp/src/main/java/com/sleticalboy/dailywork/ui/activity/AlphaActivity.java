package com.sleticalboy.dailywork.ui.activity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;

/**
 * Created on 18-5-29.
 *
 * @author sleticalboy
 * @description 控件透明度研究
 */
public class AlphaActivity extends BaseActivity {

    private static final String TAG = "AlphaActivity";

    private TextView tvShow;
    private SeekBar seekBar;
    private Button touchButton;
    private final int[] mLocation = new int[2];

    @Override
    protected int layoutResId() {
        return R.layout.activity_alpha;
    }

    @Override
    protected void initView() {
        tvShow = (TextView) findViewById(R.id.tv_show);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        touchButton = (Button) findViewById(R.id.touch_button);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged() progress = [" + progress + "], fromUser = [" + fromUser + "]");
                tvShow.setAlpha(progress / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStartTrackingTouch() called with: seekBar = [" + seekBar + "]");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "onStopTrackingTouch() called with: seekBar = [" + seekBar + "]");
            }
        });
        touchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchButton.getLocationInWindow(mLocation);
                Log.d(TAG, "mLocation[0]:" + mLocation[0]);
                Log.d(TAG, "mLocation[1]:" + mLocation[1]);
                Log.d(TAG, "touchButton.getMeasuredWidth():" + touchButton.getMeasuredWidth());
                Log.d(TAG, "touchButton.getMeasuredHeight():" + touchButton.getMeasuredHeight());
                return false;
            }
        });
    }

    @Override
    protected void initData() {

    }
}
