package com.sleticalboy.dailywork.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

public class FaceView2 extends AppCompatImageView {

    private static final String TAG = "AEYE_PREVIEW";
    private Bitmap mBitmap = null;
    private Bitmap mBitmapTemp = null;

    public FaceView2(Context context) {
        super(context);
    }

    public FaceView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void drawFaceRect(Rect faceRect, int width, int height, boolean bMirror) {

        Rect curfaceRect = new Rect();

        Log.e("xiaomin", "width = " + width + " height = " + height);

        if (mBitmapTemp == null) {
            mBitmapTemp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        }

        if (checkRect(faceRect, width, height)) {

            Log.e("xiaomin", "top = " + faceRect.top + " left = " + faceRect.left + " bottom = "
                    + faceRect.bottom + " right = " + faceRect.right);

//            curfaceRect.left = faceRect.left;
//            curfaceRect.right = faceRect.right;
//            curfaceRect.top = faceRect.top;
//            curfaceRect.bottom = faceRect.bottom;

            curfaceRect = faceRect;

            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }

            mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(mBitmap);
            int bitmapWidth;
            if (bMirror) {
                int left = width - curfaceRect.right;
                bitmapWidth = width - curfaceRect.left;
                curfaceRect.left = left;
                curfaceRect.right = bitmapWidth;
            }

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(3.0F);
            paint.setColor(Color.parseColor("#00fc45"));
            paint.setStyle(Style.STROKE);
            canvas.drawRect(curfaceRect, paint);
            bitmapWidth = mBitmap.getWidth();
            int bitmapHeight = mBitmap.getHeight();
            setImageBitmap(mBitmap);
        } else {
            setImageBitmap(mBitmapTemp);

            Log.e("xiaomin", "faceRect == null");
        }
    }

    private boolean checkRect(Rect faceRect, int width, int height) {
        return faceRect != null
                && faceRect.left < faceRect.right
                && faceRect.top < faceRect.bottom
                && faceRect.left >= 0
                && faceRect.right <= width
                && faceRect.top >= 0
                && faceRect.bottom <= height;
    }
}
