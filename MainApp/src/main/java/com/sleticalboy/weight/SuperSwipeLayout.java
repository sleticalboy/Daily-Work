package com.sleticalboy.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;

/**
 * Created on 18-6-7.
 *
 * @author leebin
 * @description
 */
public class SuperSwipeLayout extends ViewGroup {

    public SuperSwipeLayout(Context context) {
        this(context, null);
    }

    public SuperSwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuperSwipeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final VelocityTracker velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        velocityTracker.computeCurrentVelocity(1000);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                final float xVelocity = Math.abs(velocityTracker.getXVelocity());
                final float yVelocity = Math.abs(velocityTracker.getYVelocity());
                Log.d("SuperSwipeLayout", "xVelocity:" + xVelocity);
                Log.d("SuperSwipeLayout", "yVelocity:" + yVelocity);
                break;
            default:
                break;
        }
        velocityTracker.clear();
        velocityTracker.recycle();
        return super.onTouchEvent(event);
    }
}
