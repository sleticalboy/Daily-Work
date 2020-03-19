package com.sleticalboy.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * Created on 18-3-1.
 *
 * @author leebin
 * @version 1.0
 * @description
 */
public class FaceContourView extends AppCompatImageView {

    private Paint mPaint;
    private Path mPath;
    private float mScreenWidthCenter;

    public FaceContourView(Context context) {
        this(context, null);
    }

    public FaceContourView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceContourView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(4);

        mPath = new Path();

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        Log.d("FaceContourView", "dm.density:" + dm.density); // 3
        Log.d("FaceContourView", "dm.densityDpi:" + dm.densityDpi); // 480
        Log.d("FaceContourView", "dm.widthPixels:" + dm.widthPixels); // 1080
        Log.d("FaceContourView", "dm.heightPixels:" + dm.heightPixels); // 1920
        mScreenWidthCenter = dm.widthPixels / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBeforeSuper(canvas);
        super.onDraw(canvas);
        drawFaceContour(canvas);
    }

    private void drawBeforeSuper(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#5c5c5c"));

        // 画脸部
        // 起点 天灵
        mPath.moveTo(mScreenWidthCenter, 200);
        // 左上四分之一 绝对移动：第一个点是控制点，第二个点是终点
        mPath.quadTo(mScreenWidthCenter / 2, 200, mScreenWidthCenter / 2, 400); // 左耳
        // 左下四分之一 相对移动：第一个点是控制点，第二个点是终点
        mPath.rQuadTo(0, 500, mScreenWidthCenter / 2, 500); // 下巴
        // 右下四分之一 相对移动
        mPath.rQuadTo(mScreenWidthCenter / 2, 0, mScreenWidthCenter / 2, -500); // 右耳
        // 右上四分之一 相对移动
        mPath.rQuadTo(0, -200, -mScreenWidthCenter / 2, -200); // 天灵
        mPath.close();
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setStyle(Paint.Style.FILL);
        mPath.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawFaceContour(Canvas canvas) {
        // 画脸部边缘轮廓
        mPath.reset();
        // 起点 天灵
        mPath.moveTo(mScreenWidthCenter, 200);
        // 左上四分之一 绝对移动：第一个点是控制点，第二个点是终点
        mPath.quadTo(mScreenWidthCenter / 2, 200, mScreenWidthCenter / 2, 400); // 左耳
        // 左下四分之一 相对移动：第一个点是控制点，第二个点是终点
        mPath.rQuadTo(0, 500, mScreenWidthCenter / 2, 500); // 下巴
        // 右下四分之一 相对移动
        mPath.rQuadTo(mScreenWidthCenter / 2, 0, mScreenWidthCenter / 2, -500); // 右耳
        // 右上四分之一 相对移动
        mPath.rQuadTo(0, -200, -mScreenWidthCenter / 2, -200); // 天灵
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
    }
}
