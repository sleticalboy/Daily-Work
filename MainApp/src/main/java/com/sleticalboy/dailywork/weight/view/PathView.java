package com.sleticalboy.dailywork.weight.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sleticalboy.dailywork.R;

/**
 * Created on 18-3-1.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 绘图：Path 类练习使用
 */
public class PathView extends View {

    private Paint mPaint;
    private Path mPath;
    private RectF mOval;
    private Bitmap mBitmap;
    private Shader mShader;

    public PathView(Context context) {
        this(context, null);
    }

    public PathView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE); // 画线，填充，默认是填充
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4);

        mPath = new Path();
        mOval = new RectF();
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_test_drawable);
        mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 颜色填充
        canvas.drawColor(Color.parseColor("#88880000"));

        // 绘制一条线
        line(canvas);

        // 绘制一个正方形
        line2Rect(canvas);
        // 或者
        rect(canvas);

        // 绘制一个圆形
        circle(canvas);

        // 曲线
        bezierlLine(canvas);

        // 点
        point(canvas);

        // 画椭圆
        oval(canvas);

        // 圆角矩形
        roundRect(canvas);

        // 扇形 弧
        drawArc(canvas);

        // 自定义图形
        drawHeart(canvas);

        // 绘制文字
        drawText(canvas);

        // 绘制 Bitmap
        canvas.drawBitmap(mBitmap, 100, 1400, mPaint);
        mPaint.setShader(mShader);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(800, 600, 200, mPaint);
    }

    private void drawText(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(72);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(2);
        canvas.drawText("hello paint, path and canvas", 0, 1080, mPaint);
    }

    private void drawHeart(Canvas canvas) {
        mPath.reset();
        mPath.addArc(200, 200, 400, 400, -225, 225);
        mPath.arcTo(400, 200, 600, 400, -180, 225, false);
        mPath.lineTo(400, 542);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawArc(Canvas canvas) {
        mOval.left += 200;
        mOval.top += 300;
        mOval.right -= 200;
        mOval.bottom += 300;
        mPaint.setColor(Color.parseColor("#99988812"));
        mPaint.setStyle(Paint.Style.FILL);
        // mOval: 范围，100：起始，100：扫过的度数
        // userCenter：true 使用圆心，画出来就是扇形， false 画出来就是弧形
        canvas.drawArc(mOval, 100, 100, true, mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.MAGENTA);
        canvas.drawArc(mOval, -100, 180, false, mPaint);
    }

    private void roundRect(Canvas canvas) {
        mPaint.setColor(Color.parseColor("#233298"));
        mOval.left += 200;
        mOval.top += 300;
        mOval.right += 200;
        mOval.bottom += 300;
        canvas.drawRoundRect(mOval, 50, 50, mPaint);
    }

    private void oval(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#982378"));
        mOval.set(50, 800, 800, 1000);
        canvas.drawOval(mOval, mPaint);
    }

    private void point(Canvas canvas) {
        //        mPaint.reset(); // 重置画笔
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(20); // 设置线条宽度
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 设置形状
        canvas.drawPoint(400, 400, mPaint);
    }

    private void bezierlLine(Canvas canvas) {
        mPaint.setColor(Color.YELLOW);
        mPath.reset();
        mPath.moveTo(0, 300); // 起点
        mPath.quadTo(150, 750, 300, 300); // 第一个点：控制点，第二个点：终点
        canvas.drawPath(mPath, mPaint);
    }

    private void circle(Canvas canvas) {
        mPath.reset();
        mPaint.setColor(Color.GREEN);
        canvas.drawCircle(800, 175, 125, mPaint);
    }

    private void rect(Canvas canvas) {
        mPath.reset();
        mPaint.setColor(Color.BLUE);
        canvas.drawRect(350, 0, 610, 300, mPaint);
    }

    private void line2Rect(Canvas canvas) {
        mPaint.setColor(Color.RED);
        mPath.reset();
        mPath.moveTo(0, 0);
        mPath.lineTo(0, 0);
        mPath.lineTo(300, 0);
        mPath.lineTo(300, 300);
        mPath.lineTo(0, 300);
        canvas.drawPath(mPath, mPaint);
    }

    private void line(Canvas canvas) {
        mPaint.setColor(Color.YELLOW);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.reset();
        mPath.moveTo(0, 600);
        mPath.lineTo(1000, 600); // 绝对坐标
        mPath.rLineTo(-200, -300); // 相对于上一个坐标点坐标
        mOval.set(400, 400, 900, 900);
        // forceMoveTo: false 连笔，true，抬起画笔
        mPath.arcTo(mOval, -90, 270, false);
        canvas.drawPath(mPath, mPaint);
    }
}
