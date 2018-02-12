package com.sleticalboy.dailywork.ui.activity;

import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.weight.xrecycler.adapter.XRecyclerAdapter;
import com.sleticalboy.dailywork.weight.xrecycler.helper.StartSnapHelper;

/**
 * Created on 18-2-7.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class WheelRVActivity extends BaseActivity {

    private static final String TAG = "WheelRVActivity";

    private Integer[] mImagesId = {
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
            R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher,
    };

    private RecyclerView mRecyclerView;
    private int mCurrentPos = Integer.MAX_VALUE >> 1;
    private static final int INTERVAL_TIME = 3000;

    private boolean mIsPause = false;

    private Handler mHandler = new Handler();
    private Runnable mWheelTask = new Runnable() {
        @Override
        public void run() {
            if (!mIsPause) {
                mRecyclerView.smoothScrollToPosition(++mCurrentPos);
                mHandler.postDelayed(this, INTERVAL_TIME);
            }
        }
    };

    @Override
    protected void initData() {
    }

    @Override
    public void onAttachedToWindow() {
        mIsPause = false;
        startWheel();
    }

    @Override
    public void onDetachedFromWindow() {
        mIsPause = true;
        stopWheel();
    }

    // 开启定时轮播
    private void startWheel() {
        mHandler.postDelayed(mWheelTask, INTERVAL_TIME / 10);
    }

    // 停止定时轮播
    private void stopWheel() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPause = true;
        stopWheel();
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        ItemAdapter adapter = new ItemAdapter(this, mImagesId);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(mCurrentPos);
//        new LinearSnapHelper().attachToRecyclerView(mRecyclerView);
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
//        new StartSnapHelper().attachToRecyclerView(mRecyclerView);

        mRecyclerView.setOnTouchListener(new MyOnTouchListener());
        mRecyclerView.addOnScrollListener(new MyOnScrollListener());
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_wheel_rv;
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mIsPause = true;
                    break;
                default:
                    mIsPause = false;
                    break;
            }
            return false;
        }
    }

    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (mIsPause && dy < 0) {
                mCurrentPos--;
            } else if (mIsPause && dy > 0) {
                mCurrentPos++;
            }
        }
    }

}
