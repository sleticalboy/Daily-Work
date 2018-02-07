package com.sleticalboy.dailywork.weight.xrefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.weight.xrefresh.interfaces.IFooterView;

/**
 * Created on 18-2-3.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class FooterView extends FrameLayout implements IFooterView {

    public FooterView(@NonNull Context context) {
        this(context, null);
    }

    public FooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_refresh_footer, this);
    }

    @Override
    public void begin() {

    }

    @Override
    public void progress(long progress, long total) {

    }

    @Override
    public void finish(long progress, long total) {

    }

    @Override
    public void loading() {

    }

    @Override
    public void hidden() {

    }

    @Override
    public View get() {
        return this;
    }
}
