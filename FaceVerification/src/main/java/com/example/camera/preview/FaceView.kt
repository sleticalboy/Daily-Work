package com.example.camera.preview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.hardware.Camera
import android.util.AttributeSet
import android.widget.ImageView

import com.example.camera.R
import com.example.camera.util.CameraUtil

/**
 * Created by renlei
 * DATE: 15-11-11
 * Time: 上午11:34
 * Email: renlei0109@yeah.net
 */
class FaceView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    private var mFaces: Array<Camera.Face>? = null
    private val mMatrix = Matrix()
    // 如果是前置摄像头需要做一次镜像
    private var mIsMirror = false

    private val rectF = RectF()
    private var mFaceIndicator: Drawable? = null

    init {
        // initPaint();
        mFaceIndicator = context.resources.getDrawable(R.drawable.ic_face_find_2)
    }

    fun setFaces(faces: Array<Camera.Face>) {
        mFaces = faces
        invalidate()
    }

    fun clearFaces() {
        mFaces = null
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (mFaces == null || mFaces!!.isEmpty()) {
            return
        }
        mIsMirror = CameraUtil.instance.cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT
        canvas.save()
        prepareMatrix()
        // Matrix.postRotate默认是顺时针
        mMatrix.postRotate(0f)
        // Canvas.rotate()默认是逆时针
        canvas.rotate(0f)
        for (face in mFaces!!) {
            rectF.set(face.rect)
            mMatrix.mapRect(rectF)
            mFaceIndicator!!.setBounds(
                    Math.round(rectF.left), Math.round(rectF.top),
                    Math.round(rectF.right), Math.round(rectF.bottom))
            mFaceIndicator!!.draw(canvas)
        }
        canvas.restore()
        super.onDraw(canvas)
    }

    /**
     *
     * Here is the matrix to convert driver coordinates to View coordinates
     * in pixels.
     * <pre>
     * Matrix matrix = new Matrix();
     * CameraInfo info = CameraHolder.instance().getCameraInfo()[cameraId];
     * // Need mIsMirror for front mCamera.
     * boolean mIsMirror = (info.facing == CameraInfo.CAMERA_FACING_FRONT);
     * matrix.setScale(mIsMirror ? -1 : 1, 1);
     * // This is the value for android.hardware.Camera.setDisplayOrientation.
     * matrix.postRotate(displayOrientation);
     * // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
     * // UI coordinates range from (0, 0) to (width, height).
     * matrix.postScale(view.getWidth() / 2000f, view.getHeight() / 2000f);
     * matrix.postTranslate(view.getWidth() / 2f, view.getHeight() / 2f);
    </pre> *
     */
    private fun prepareMatrix() {
        mMatrix.setScale((if (mIsMirror) -1 else 1).toFloat(), 1f)
        mMatrix.postRotate(9f)
        mMatrix.postScale(width / 2000f, height / 2000f)
        mMatrix.postTranslate(width / 2f, height / 2f)
    }

    private fun initPaint() {
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //		int color = Color.rgb(0, 150, 255);
        val color = Color.rgb(98, 212, 68)
        //		mLinePaint.setColor(Color.RED);
        linePaint.color = color
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 5f
        linePaint.alpha = 180
    }
}
