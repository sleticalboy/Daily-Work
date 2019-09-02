package com.binlee.emoji.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.GridView;

import androidx.viewpager.widget.ViewPager;

/**
 * Created on 19-7-17.
 *
 * @author leebin
 */
public final class EmojiViewPager extends ViewPager {
    
    private static final String TAG = "EmojiViewPager";
    private ViewPager mParentVp;
    
    public EmojiViewPager(Context context) {
        this(context, null);
    }
    
    public EmojiViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mParentVp == null) {
            mParentVp = findParentViewPager(this);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getChildCount() > 1 && mParentVp != null) {
                    mParentVp.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                // if (hasChildHandled(event)) {
                //     return false;
                // }
                break;
            case MotionEvent.ACTION_UP:
                // hasChildHandled(event);
                break;
        }
        final boolean superHandled = super.onTouchEvent(event);
        // Log.d(TAG, "onTouchEvent() handled: " + superHandled + ", event: " + event + ", vp: " + mParentVp);
        return superHandled;
    }
    
    private ViewPager findParentViewPager(View child) {
        final ViewParent parent = child.getParent();
        if (parent instanceof ViewPager) {
            return (ViewPager) parent;
        } else if (parent instanceof View) {
            return findParentViewPager(((View) parent));
        }
        return null;
    }
    
    private boolean hasChildHandled(final MotionEvent event) {
        for (int i = 0, count = getChildCount(); i < count; i++) {
            final View child = getChildAt(i);
            if (child instanceof GridView) {
                Log.d(TAG, i + " child:" + child);
                if (child.onTouchEvent(event)) {
                    return true;
                }
            }
        }
        return false;
    }
}
