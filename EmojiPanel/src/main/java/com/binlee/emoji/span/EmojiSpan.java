package com.binlee.emoji.span;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

public final class EmojiSpan extends ImageSpan {

    private int bgColor = Color.TRANSPARENT;

    public EmojiSpan(Drawable d, String source) {
        super(d, source);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                     @NonNull Paint paint) {
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        final Drawable d = getDrawable();
        canvas.save();
        // 计算y方向的位移
        int transY = (y + fm.descent + y + fm.ascent) / 2 - d.getBounds().bottom / 2;
        final int oldColor = paint.getColor();
        paint.setColor(bgColor);
        canvas.drawRect(x, top, x + d.getBounds().right, bottom, paint);
        // 绘制图片位移一段距离
        canvas.translate(x, transY);
        paint.setColor(oldColor);
        d.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        final Drawable d = getDrawable();
        final Rect rect = d.getBounds();
        if (fm != null) {
            final Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            final int fontHeight = fontMetricsInt.bottom - fontMetricsInt.top;
            final int drawableHeight = rect.bottom - rect.top;
            final int top = drawableHeight / 2 - fontHeight / 4;
            final int bottom = drawableHeight / 2 + fontHeight / 4;
            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    public void updateColor(int color) {
        bgColor = color;
        getDrawable().invalidateSelf();
    }
}
