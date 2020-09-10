package com.grandstream.gmd.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.grandstream.gmd.R;

public class ProgressView extends View {

    private @interface Status {
        int STATUS_IDLE = 0x10;
        int STATUS_LOADING = 0x11;
        int STATUS_SUCCESS = 0x12;
    }

    @Status
    private int mStatus = Status.STATUS_IDLE;

    private float progressWidth = 8;
    private float progressRadius;
    //开始角度
    private int startAngle = -90;
    //最小角度
    private int minAngle = -90;
    //扫描角度
    private int sweepAngle = 120;
    //当前角度
    private int curAngle = 0;
    //追踪Path的坐标
    private PathMeasure mPathMeasure;
    //画圆的Path
    private Path mPathCircle;
    // 截取 PathMeasure 中的 path
    private Path mPathCircleDst;
    private Path successPath;
    private float circleValue;
    private float successValue;
    private Paint mPaint;
    private ValueAnimator mValueAnimator;

    private RectF mRectF;
    private int mLoadingColor, mFinishColor;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = null;
        try {
            ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
            progressRadius = ta.getDimensionPixelSize(R.styleable.ProgressView_progress_size, 70);
            Log.d("ProgressView", "progressRadius:" + progressRadius);
            mLoadingColor = ta.getColor(R.styleable.ProgressView_loading_color, R.color.progress_loading);
            mFinishColor = ta.getColor(R.styleable.ProgressView_finish_color, R.color.progress_finish);
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
        initPaint();
        initPath();
        initAnim();
    }

    private void initPaint() {
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(progressWidth);

        mPaint.setDither(true);
        mPaint.setAntiAlias(true);

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    private void initPath() {
        mPathCircle = new Path();
        mPathMeasure = new PathMeasure();
        mPathCircleDst = new Path();
        successPath = new Path();
    }

    private void initAnim() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1);
        mValueAnimator.addUpdateListener(animation -> {
            circleValue = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            width = size;
        } else {
            //直径
            width = (int) (2 * progressRadius + progressWidth + getPaddingLeft() + getPaddingRight());
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            height = size;
        } else {
            height = (int) (2 * progressRadius + progressWidth + getPaddingTop() + getPaddingBottom());
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStatus == Status.STATUS_LOADING) {
            canvas.translate(getPaddingLeft(), getPaddingTop());
            if (startAngle == minAngle) {
                sweepAngle += 6;
            }
            if (sweepAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (sweepAngle > 20) {
                    //保持结束位置不变
                    sweepAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                startAngle %= 360;
                minAngle = startAngle;
                sweepAngle = 20;
            }

            canvas.rotate(curAngle += 4, progressRadius, progressRadius);
            // 定义的圆弧的形状和大小的界限
            if (mRectF == null) {
                mRectF = new RectF();
            }
            mRectF.set(0, 0, progressRadius * 2, progressRadius * 2);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(progressWidth);
            mPaint.setColor(mLoadingColor);

            canvas.drawArc(mRectF, startAngle, sweepAngle, false, mPaint);
            invalidate();
        } else if (mStatus == Status.STATUS_SUCCESS) {

            // canvas.translate(0, 0);
            // mPathCircle.addCircle(getWidth() >> 1, getWidth() >> 1, progressRadius, Path.Direction.CW);
            // mPathMeasure.setPath(mPathCircle, false);
            // 截取 path 并保存到 mPathCircleDst 中
            // mPathMeasure.getSegment(0, circleValue * mPathMeasure.getLength(), mPathCircleDst, true);
            // canvas.drawPath(mPathCircleDst, mPaint);

            // 画圆形背景
            mPaint.setColor(mFinishColor);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(getWidth() >> 1, getWidth() >> 1, progressRadius, mPaint);

            // 表示圆画完了,可以画钩了
            // if (circleValue == 1) {
            if (circleValue >= 0) {
                // 在 progressRadius = 70 的标准上计算的
                successPath.moveTo(50 - offset(), 70 - offset());
                successPath.lineTo(71 - offset(), 90 - offset());
                successPath.lineTo(108 - offset(), 55 - offset());
                //
                // mPathMeasure.nextContour();
                // mPathMeasure.setPath(successPath, false);
                // mPathMeasure.getSegment(0, successValue * mPathMeasure.getLength(), mPathCircleDst, true);

                mPaint.setColor(getContext().getColor(android.R.color.white));
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(progressWidth);
                // canvas.drawPath(mPathCircleDst, mPaint);
                canvas.drawPath(successPath, mPaint);
            }
        }
    }

    private float offset() {
        return (progressRadius / 70F) * (70 - progressRadius);
    }

    private void clearState() {
        startAngle = -90;
        minAngle = -90;
        sweepAngle = 120;
        curAngle = 0;
        circleValue = 0;
        successValue = 0;
        initPath();
    }

    private void setStatus(@Status int status) {
        mStatus = status;
    }

    public void loadLoading() {
        clearState();
        setStatus(Status.STATUS_LOADING);
        invalidate();
    }

    public void loadSuccess() {
        setStatus(Status.STATUS_SUCCESS);
        // startSuccessAnim();
    }

    private void startSuccessAnim() {
        ValueAnimator success = ValueAnimator.ofFloat(0f, 1.0f);
        success.addUpdateListener(animation -> {
            successValue = (float) animation.getAnimatedValue();
            invalidate();
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(success).after(mValueAnimator);
        animatorSet.setDuration(500);
        animatorSet.start();
    }
}
