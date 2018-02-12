package com.sleticalboy.dailywork.ui.activity;

import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
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
    private boolean mIsFirst = true;

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
        mHandler.postDelayed(mWheelTask, 0);
    }

    // 停止定时轮播
    private void stopWheel() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPause = true;
        mIsFirst = false;
        stopWheel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPause = false;
        if (!mIsFirst)
            startWheel();
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        layoutManager.setReverseLayout(false);
        mRecyclerView.setLayoutManager(layoutManager);
        ItemAdapter adapter = new ItemAdapter(this, mImagesId);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.scrollToPosition(mCurrentPos);
//        new LinearSnapHelper().attachToRecyclerView(mRecyclerView);
        new PagerSnapHelper().attachToRecyclerView(mRecyclerView);
//        new StartSnapHelper().attachToRecyclerView(mRecyclerView);

        adapter.setOnItemClickListener(new XRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(WheelRVActivity.this, "click :" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_wheel_rv;
    }
}
