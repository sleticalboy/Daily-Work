package com.sleticalboy.dailywork.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.manager.CameraManager;

import java.io.File;

/**
 * Created on 18-2-27.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class LiveCameraActivity extends AppCompatActivity {

    private static final String TAG = "LiveCameraActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_camera);
        initView();
    }

    private void initView() {
        TextureView liveView = findViewById(R.id.mLiveView);
        liveView.setSurfaceTextureListener(new CameraManager.SimpleSurfaceTextureListener());
        ImageButton takePicBtn = findViewById(R.id.mTakePicBtn);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        // take photos
        CameraManager.getInstance().takePicture(new CameraManager.OnPictureTakenCallback() {
            @Override
            public void onSuccess(File picture) {
                Log.d(TAG, picture.getPath());
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }

}
