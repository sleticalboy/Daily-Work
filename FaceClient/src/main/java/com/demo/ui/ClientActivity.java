package com.demo.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.demo.R;
import com.demo.manager.SSLClientManager;

import java.lang.ref.WeakReference;

import koal.ssl.IAutoService;

/**
 * ClientActivity演示了调用SSL客户端接口，接收广播消息等操作
 * <p>
 * SSL客户端接口调用步骤：
 * 1. 将IAutoService.aidl放到src/koal/ssl目录下
 * 2. 在Activity中创建IAutoService对象
 * 2. 创建ServiceConnection链接点并初始化IAutoService对象
 * 3.onCreate中先调用startService，再调用bindService
 * 4.onDestroy中调用unbindService
 * <p>
 * 接收广播消息（可选）：
 * 1. 在Activity中创建广播接收器
 * 2. 在onCreate中注册广播过滤器
 * 3. 在onDestroy中注销广播接收器
 * 注：广播接收器中不可进行大量耗时的操作，一般将此类操作放入线程中运行
 * <p>
 * 接口定义：
 * interface IAutoService2 {
 * <p>
 * 获取应用
 * 返回值：
 * 应用名称1=监听地址:监听端口
 * 应用名称2=监听地址:监听端口
 * ...
 * 应用名称n=监听地址:监听端口
 * String getApps();
 * <p>
 * <p>
 * 获取证书项信息
 * [IN]opt:0=证书内容（Base64编码），1=SN，2=CN，3=DN
 * DN中的对应项：
 * "CN"	    姓名
 * "T" 	    TF卡标识号
 * "G"		警号
 * "ALIAS"	身份证号码
 * "S"		省
 * "L"		市
 * "O"		组织
 * "OU"	    机构
 * "E"		电子邮件
 * "I"		容器名称
 * 返回值：指定项信息
 * String getCertInfo(int opt);
 * <p>
 * 服务是否已启动
 * 返回值：true=已启动，false=未启动
 * boolean isStarted();
 * <p>
 * 设置服务器地址
 * [IN]ip： 服务器地址
 * [IN]port：服务器端口
 * void setServerAddr(String ip, String port);
 * <p>
 * 启动服务
 * void start();
 * <p>
 * 停止服务
 * void stop();
 * <p>
 * 自动升级
 * void upgrade();
 */
public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClientActivity";

    private final static int REQUEST_START_SERVICE = 0;

    private SSLClientManager mClientManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        mClientManager = SSLClientManager.getInstance();
        mClientManager.setHandler(new MyHandler(this));
        mClientManager.start(this);
    }

    private void initView() {
        setContentView(R.layout.main);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);
        findViewById(R.id.btnApps).setOnClickListener(this);
        findViewById(R.id.btnCertInfo).setOnClickListener(this);
        findViewById(R.id.btnInterfaceExtend).setOnClickListener(this);
        findViewById(R.id.btnQuit).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消绑定SSL服务
        mClientManager.destroy(this);
        mClientManager = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStart:
                if (!mClientManager.isStarted()) {
//                    mClientManager.setTFModel("F-model");
//                    mClientManager.setCurCert("filecert.pfx");
//                    mClientManager.setPin("111111", true);
                    mClientManager.restart();
                }
                break;

            case R.id.btnStop:
                mClientManager.stop();
                break;

            case R.id.btnApps:
                appendLog(mClientManager.getApps());
                break;

            case R.id.btnCertInfo:
                appendLog("SN:" + mClientManager.getCertInfo(1));
                appendLog("CN:" + mClientManager.getCertInfo(2));
                appendLog("DN:" + mClientManager.getCertInfo(3));
                appendLog("SHECA:" + mClientManager.getCertInfo(4));
                break;

            // 此处为扩展接口的使用，具体使用请参考文档。
            case R.id.btnInterfaceExtend:
                String[] config = SSLClientManager.CONFIG;
                String[] devInfo = SSLClientManager.DEV_INFO;
                String[] serviceInfo = SSLClientManager.SERVICE_INFO;

                appendLog("certBase64:" + mClientManager.getCertInfo(0));
                for (int i = 0; i < config.length; i++) {
                    appendLog(config[i] + ":" + mClientManager.getConfig(config[i]));
                }
                for (int i = 0; i < devInfo.length; i++) {
                    appendLog(devInfo[i] + ":" + mClientManager.getDevInfo(devInfo[i]));
                }
                for (int i = 0; i < serviceInfo.length; i++) {
                    appendLog(serviceInfo[i] + ":" + mClientManager.getServiceInfo(serviceInfo[i]));
                }
                break;
            case R.id.btnQuit:
                mClientManager.quit();
                finish();
                break;
            default:
                break;
        }
    }

    void appendLog(String strLog) {
        Log.d(TAG, strLog);
    }

    private class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        public MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ClientActivity activity = (ClientActivity) mActivity.get();
            String data = msg.getData().getString(SSLClientManager.MSG_KEY);

            switch (msg.what) {
                case SSLClientManager.MSG_SHOW_LOG: // 向Log控件输出日志
                    activity.appendLog(data);
                    break;
                case SSLClientManager.MSG_UPGRADE: // 弹出对话框，询问是否升级
                    activity.checkUpgrade(data);
                    break;
                default:
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
}

