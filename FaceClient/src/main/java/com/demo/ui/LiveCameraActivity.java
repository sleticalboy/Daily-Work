package com.demo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

import com.demo.R;
import com.demo.bean.Police;
import com.demo.manager.CameraManager;
import com.demo.manager.SSLClientManager;

import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created on 18-2-27.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 人脸识别预览界面
 */
public class LiveCameraActivity extends AppCompatActivity {

    private static final String TAG = "LiveCameraActivity";

    private SSLClientManager mClientManager;
    private Handler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_camera);
        mClientManager = SSLClientManager.getInstance();
        initView();
        mClientManager.setHandler(mHandler);
        mClientManager.start(this);
        if (!mClientManager.isStarted()) {
//            mClientManager.restart();
            initClient();
        }
        Police police = new Police();
        police.setCn("");
        police.setAlias("");
        police.setG("");
        police.setT("");
    }

    private void initClient() {
//        mClientManager.setCurCert("file-cert.pfx"); // 全路径
//        mClientManager.setTFModel("F-model");
//        mClientManager.setPin("111111", true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClientManager.destroy(this);
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
        CameraManager.getInstance().takePicture(new CameraManager.OnPictureTakenCallback() {
            @Override
            public void onSuccess(File picture) {
                // TODO: 18-2-27 上传服务器进行登录验证
                Log.d(TAG, picture.getPath());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "onFailure: ", throwable);
            }
        });
    }

    private static class MyHandler extends Handler {

        WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final LiveCameraActivity activity = (LiveCameraActivity) mActivity.get();
            final String data = msg.getData().getString(SSLClientManager.MSG_KEY);
            switch (msg.what) {
                case SSLClientManager.MSG_SHOW_LOG:
                    activity.printLog(data);
                    break;
                case SSLClientManager.MSG_UPGRADE:
                    activity.checkUpgrade(data);
                    break;
            }
        }
    }

    private void checkUpgrade(String data) {
        new AlertDialog
                .Builder(this)
                .setTitle("自动升级")
                .setMessage("最新版本：" + data + ",是否立即升级？")
                .setCancelable(true)
                .setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SSLClientManager.getInstance().upgrade();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void printLog(String data) {
    }
}
