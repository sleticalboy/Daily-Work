package com.binlee.emoji.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

/**
 * Created on 19-7-20.
 *
 * @author leebin
 */
public final class EmojiGridView extends GridView {
    
    private static final String TAG = "EmojiGridView";
    private static final int LONG_PRESS = 2;
    private OnPressListener mOnPress;
    
    // private int mActivePid = MotionEvent.INVALID_POINTER_ID;
    private int mLastPos = INVALID_POSITION;
    private long mDownTime = -1;
    private boolean mIsLongPressUp = false;
    private final Handler mEventHandler = new Handler(msg -> {
        if (msg.what == LONG_PRESS) {
            handlePressEvent(msg.arg1, msg.arg2);
        }
        return true;
    });
    
    public EmojiGridView(Context context) {
        this(context, null);
    }
    
    public EmojiGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public EmojiGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        // Log.d(TAG, "onTouchEvent() called with: event = [" + event + "]");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // mActivePid = event.getPointerId(0);
                mDownTime = event.getDownTime();
                mIsLongPressUp = false;
                mEventHandler.removeMessages(LONG_PRESS);
                final Message msg = Message.obtain(mEventHandler, LONG_PRESS);
                msg.arg1 = (int) event.getX();
                msg.arg2 = ((int) event.getY());
                mEventHandler.sendMessageAtTime(msg, mDownTime + 500L);
                break;
            case MotionEvent.ACTION_MOVE:
                mIsLongPressUp = false;
                mEventHandler.removeMessages(LONG_PRESS);
                final long duration = event.getEventTime() - mDownTime;
                // Log.d(TAG, "onTouchEvent() duration: " + duration);
                if (duration > 500L && handlePressEvent(((int) event.getX()), ((int) event.getY()))) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mEventHandler.removeMessages(LONG_PRESS);
                // mActivePid = MotionEvent.INVALID_POINTER_ID;
                mIsLongPressUp = event.getEventTime() - mDownTime > 500L;
                mLastPos = INVALID_POSITION;
                mDownTime = -1;
                if (mOnPress != null) {
                    mOnPress.onCancelPress();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    public void setOnItemLongClickListener(final OnItemLongClickListener listener) {
        super.setOnItemLongClickListener(null);
    }
    
    @Override
    public boolean performItemClick(final View view, final int position, final long id) {
        if (mIsLongPressUp) {
            /* 返回 true：长按抬起时不触发 onItemClick 事件 */
            return true;
        }
        return super.performItemClick(view, position, id);
    }
    
    public void setOnPressListener(final OnPressListener listener) {
        mOnPress = listener;
    }
    
    private boolean handlePressEvent(final int x, final int y) {
        final int pos = mOnPress != null ? pointToPosition(x, y) : INVALID_POSITION;
        Log.d(TAG, "handlePressEvent() pos: " + pos + ", pressCallback: " + mOnPress);
        if (pos != INVALID_POSITION && pos != mLastPos) {
            mOnPress.onLongPress(pos);
            mLastPos = pos;
            return true;
        } else if (pos == INVALID_POSITION && mOnPress != null) {
            mOnPress.onCancelPress();
            mLastPos = pos;
            return true;
        }
        return false;
    }
    
    public interface OnPressListener {
        /**
         * 长按回调
         *
         * @param position 按住的位置
         */
        void onLongPress(int position);
        
        /**
         * 手指离开屏幕回调
         */
        void onCancelPress();
    }
}
