package com.sleticalboy.dailywork.weight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created on 18-2-26.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class MyFaceView extends AppCompatImageView {

    private static final int MAX_FACES_NUM = 3;

    private int mImageWidth;
    private int mImageHeight;
    private FaceDetector mFaceDetector;
    private Bitmap mFaceBitmap;
    private FaceDetector.Face[] mFaces = new FaceDetector.Face[MAX_FACES_NUM];
    private int mRealFaces;
    private float mEyesDistance;
    private Paint mPaint;
    private Handler mHandler = new Handler();

    public MyFaceView(Context context) {
        this(context, null);
    }

    public MyFaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
    }

    private void init() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        mFaceBitmap = BitmapFactory.decodeResource(getResources(), -1, options);
        mImageWidth = mFaceBitmap.getWidth();
        mImageHeight = mFaceBitmap.getHeight();
        mFaceDetector = new FaceDetector(mImageWidth, mImageHeight, MAX_FACES_NUM);
        mRealFaces = mFaceDetector.findFaces(mFaceBitmap, mFaces);
        Log.d("MyFaceView", "mRealFaces:" + mRealFaces);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRealFaces == 0) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mRealFaces; i++) {
                    FaceDetector.Face face = mFaces[i];
                    PointF pointF = new PointF();
                    face.getMidPoint(pointF);
                    mEyesDistance = face.eyesDistance();
                    canvas.drawRect(
                            pointF.x - mEyesDistance,
                            pointF.y - mEyesDistance,
                            pointF.x + mEyesDistance,
                            pointF.y + mEyesDistance * 1.5f,
                            mPaint
                    );
                }
            }
        });

    }
}
