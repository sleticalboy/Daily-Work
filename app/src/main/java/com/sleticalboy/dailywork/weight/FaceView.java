package com.sleticalboy.dailywork.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.util.CameraUtils;

/**
 * Created on 18-2-24.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class FaceView extends AppCompatImageView {

    private Camera.Face[] mFaces;
    private Matrix mMatrix = new Matrix();
    private boolean mIsNeedMirror = false;
    private RectF mRectF = new RectF();
    private Drawable mFaceIndicator;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mFaceIndicator = context.getResources().getDrawable(R.drawable.ic_face_find_2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mFaces == null || mFaces.length < 1) {
            return;
        }
        mIsNeedMirror = CameraUtils.getInstance().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT;
        canvas.save();
        prepareMatrix();

        mMatrix.postRotate(0);
        canvas.rotate(0);
        for (Camera.Face face : mFaces) {
            mRectF.set(face.rect);
            mMatrix.mapRect(mRectF);
            mFaceIndicator.setBounds(
                    Math.round(mRectF.left), Math.round(mRectF.top),
                    Math.round(mRectF.right), Math.round(mRectF.bottom));
            mFaceIndicator.draw(canvas);
        }
        canvas.restore();

        super.onDraw(canvas);
    }

    /**
     * <p>Here is the matrix to convert driver coordinates to View coordinates
     * in pixels.</p>
     * <pre>
     * Matrix matrix = new Matrix();
     * CameraInfo info = CameraHolder.instance().getCameraInfo()[cameraId];
     * // Need mIsMirror for front camera.
     * boolean mIsMirror = (info.facing == CameraInfo.CAMERA_FACING_FRONT);
     * matrix.setScale(mIsMirror ? -1 : 1, 1);
     * // This is the value for android.hardware.Camera.setDisplayOrientation.
     * matrix.postRotate(displayOrientation);
     * // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
     * // UI coordinates range from (0, 0) to (width, height).
     * matrix.postScale(view.getWidth() / 2000f, view.getHeight() / 2000f);
     * matrix.postTranslate(view.getWidth() / 2f, view.getHeight() / 2f);
     * </pre>
     */
    private void prepareMatrix() {
        mMatrix.setScale(mIsNeedMirror ? -1 : 1, 1);
        mMatrix.postRotate(9);
        mMatrix.postScale(getWidth() / 2000f, getHeight() / 2000f);
        mMatrix.postTranslate(getWidth() / 2f, getHeight() / 2f);
    }

    public void setFaces(Camera.Face[] faces) {
        mFaces = faces;
        invalidate();
    }

    public void clearFaces() {
        mFaces = null;
        invalidate();
    }
}
