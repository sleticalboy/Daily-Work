package com.sleticalboy.weight.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class TestView extends View {

    private static final String TAG = "TestView";
    private int mNumber = 1;
    private final Paint mPaint;

    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(32);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw() called number: " + mNumber);
        for (int i = 0; i < mNumber; i++) {
            float x = 60 * i;
            if (x > getWidth()) {
                x = x - getWidth();
            }
            canvas.drawText(Integer.toString(i), x, 30 * i, mPaint);
        }
    }

    public void update() {
        mNumber++;
        invalidate();
    }
}
