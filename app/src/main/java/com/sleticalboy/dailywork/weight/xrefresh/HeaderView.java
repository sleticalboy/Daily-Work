package com.sleticalboy.dailywork.weight.xrefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.weight.xrefresh.interfaces.IHeaderView;

/**
 * Created on 18-2-3.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class HeaderView extends FrameLayout implements IHeaderView {

    private ImageView default_ptr_flip;
    private ProgressBar default_ptr_rotate;
    private TextView refresh_tip, refresh_time;

    public HeaderView(@NonNull Context context) {
        this(context, null);
    }

    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh_header, this);
        default_ptr_flip = ((ImageView) findViewById(R.id.default_ptr_flip));
        default_ptr_rotate = ((ProgressBar) findViewById(R.id.default_ptr_rotate));
        refresh_tip = ((TextView) findViewById(R.id.refresh_tip));
        refresh_time = ((TextView) findViewById(R.id.refresh_time));
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(long progress, long total) {
        if (progress / total >= 0.9f) {
            default_ptr_flip.setRotation(180);
        } else {
            default_ptr_flip.setRotation(0);
        }
        if (progress >= total - 10) {
            refresh_tip.setText("松开以刷新");
        } else {
            refresh_tip.setText("下拉以刷新");
        }
    }

    @Override
    public void finish(long progress, long total) {

    }

    @Override
    public void loading() {
        default_ptr_flip.setVisibility(GONE);
        default_ptr_rotate.setVisibility(VISIBLE);
        refresh_tip.setText("加载中...");
        refresh_time.setText("2018-02-03 20:14:50");
    }

    @Override
    public void hidden() {
        default_ptr_flip.setVisibility(VISIBLE);
        default_ptr_rotate.setVisibility(GONE);
        refresh_tip.setText("下拉以刷新");
        refresh_time.setText("2018-02-03 20:15:03");
    }

    @Override
    public View get() {
        return this;
    }
}
