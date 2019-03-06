package com.sleticalboy.dailywork.weight.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.sleticalboy.dailywork.R;

/**
 * Created on 18-3-1.
 *
 * @author leebin
 */
public class SingleLine extends View {

    private static final int DEF_MAIN_COLOR = Color.parseColor("#e5e5e5");
    private final Paint mPaint;
    private float circlePadding = 20;
    private float radius = 10;
    private float lineWidth = 4;
    private int mainColor;

    public SingleLine(Context context) {
        this(context, null);
    }

    public SingleLine(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        readAttrs(context, attrs);
    }

    private void readAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = null;
            try {
                a = context.obtainStyledAttributes(attrs, R.styleable.SingleLine);
                circlePadding = a.getDimension(R.styleable.SingleLine_circlePadding, 20);
                radius = a.getInteger(R.styleable.SingleLine_circleRadius, 10);
                lineWidth = a.getInteger(R.styleable.SingleLine_lineWidth, 4);
                mainColor = a.getColor(R.styleable.SingleLine_lineColor, DEF_MAIN_COLOR);
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mainColor);
        mPaint.setStrokeWidth(lineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        final int cx = getWidth() >> 1;
        final int cy = getHeight() >> 1;
        canvas.drawLine(getPaddingLeft(), cy, cx - circlePadding, cy, mPaint);

        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, radius, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(cx + circlePadding, cy, cx * 2 - getPaddingRight(), cy, mPaint);
    }

    public void setRadius(int radius) {
        this.radius = radius;
        invalidate();
    }

    public void setCirclePadding(int circlePadding) {
        this.circlePadding = circlePadding;
        invalidate();
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
        invalidate();
    }

    public void setMainColor(@ColorRes int mainColor) {
        this.mainColor = getResources().getColor(mainColor);
        invalidate();
    }
}
