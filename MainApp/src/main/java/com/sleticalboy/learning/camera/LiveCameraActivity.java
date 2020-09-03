package com.sleticalboy.learning.camera;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageButton;

import com.sleticalboy.learning.R;
import com.sleticalboy.learning.base.BaseActivity;

import java.io.File;

/**
 * Created on 18-2-27.
 *
 * @author leebin
 * @version 1.0
 */
public class LiveCameraActivity extends BaseActivity {

    private static final String TAG = "LiveCameraActivity";

    @Override
    protected int layoutResId() {
        return R.layout.activity_live_camera;
    }

    @Override
    protected void initView() {
        TextureView liveView = findViewById(R.id.mLiveView);
        liveView.setSurfaceTextureListener(new CameraManager.SimpleSurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                getRxPerm().request(Manifest.permission.CAMERA)
                        .subscribe(granted -> {
                            if (granted) {
                                CameraManager.getInstance().startPreview(surface);
                            }
                        });
            }
        });
        ImageButton takePicBtn = findViewById(R.id.mTakePicBtn);
        takePicBtn.setOnClickListener(v ->
                getRxPerm().request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).subscribe(granted -> takePicture())
        );
    }

    private void takePicture() {
        // take photos
        CameraManager.getInstance().takePicture(new File("/sdcard/DCIM/Camera"),
                new CameraManager.OnPictureTakenCallback() {
                    @Override
                    public void onSuccess(File picture) {
                        Log.d(TAG, picture.getPath());
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                }
        );
    }
}
