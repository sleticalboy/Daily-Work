package com.binlee.emoji.span;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created on 19-7-29.
 *
 * @author leebin
 */
public final class CustomAtSpan extends ReplacementSpan {
    
    private final CharSequence mSource;
    
    public CustomAtSpan(final CharSequence source) {
        mSource = source;
    }
    
    @Override
    public int getSize(@NonNull final Paint paint, final CharSequence text,
                       final int start, final int end,
                       @Nullable final Paint.FontMetricsInt fm) {
        return (int) paint.measureText(mSource, 0, mSource.length());
    }
    
    @Override
    public void draw(@NonNull final Canvas canvas, final CharSequence text,
                     final int start, final int end,
                     final float x, final int top, final int y, final int bottom,
                     @NonNull final Paint paint) {
        canvas.drawText(mSource, 0, mSource.length(), x, y, paint);
    }
}
