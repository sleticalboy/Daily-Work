package com.Demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

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
public class ClientActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "ClientActivity";

    // SSL广播消息数据
    public static final String ACTION_INTENT_DATA = "data";
    // SSL广播消息：启动服务中
    public static final String ACTION_INTENT_STARTSERVER_INPROC = "koal.ssl.broadcast.startserver.inproc";
    // SSL广播消息：启动服务成功
    public static final String ACTION_INTENT_STARTSERVER_SUCCESS = "koal.ssl.broadcast.startserver.success";
    // SSL广播消息：启动服务失败
    public static final String ACTION_INTENT_STARTSERVER_FAILURE = "koal.ssl.broadcast.startserver.failure";
    // SSL广播消息：下载策略成功
    public static final String ACTION_INTENT_DOWNLOADCFG_SUCCESS = "koal.ssl.broadcast.downloadcfg.success";
    // SSL广播消息：停止服务成功
    public static final String ACTION_INTENT_STOPSERVER_SUCCESS = "koal.ssl.broadcast.stopserver.success";
    // SSL广播消息：检测到新版本，可升级
    public static final String ACTION_INTENT_UPGRADE = "koal.ssl.broadcast.upgrade";
    // SSL广播消息：网络（wifi/apn）已链接
    public static final String ACTION_INTENT_NETWORK_CONNECTED = "koal.ssl.broadcast.network.connected";
    // SSL广播消息：网络（wifi/apn）已断开
    public static final String ACTION_INTENT_NETWORK_DISCONNECTED = "koal.ssl.broadcast.network.disconnected";
    // SSL广播消息：检测应用成功
    public static final String ACTION_INTENT_CHECKAPPS_SUCCESS = "koal.ssl.broadcast.checkapps.success";
    // SSL广播消息：检测应用失败（后台应用服务器无法建立tcp链接）
    public static final String ACTION_INTENT_CHECKAPPS_FAILURE = "koal.ssl.broadcast.checkapps.failure";

    // SSL服务名称
    public static final String KOAL_SERVICE = "koal.ssl.vpn.service";

    private final static int REQUEST_START_SERVICE = 0;

    private static final String MSG_KEY = "data";
    private static final int MSG_SHOW_LOG = 1;
    private static final int MSG_UPGRADE = 2;

    private TextView txtLog;
    private ScrollView sclView;

    private SSLReceiver srvMonitor;
    private IAutoService autoService;
    private ServiceConnection serviceConnection;
    private MyHandler handler;

    private static class MyHandler extends Handler {
        WeakReference<Activity> mActivity;

        MyHandler(Activity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        public void handleMessage(android.os.Message msg) {

            final ClientActivity activity = (ClientActivity) mActivity.get();
            String data = msg.getData().getString(MSG_KEY);

            switch (msg.what) {
                case MSG_SHOW_LOG: // 向Log控件输出日志
                    activity.appendLog(data);
                    break;
                case MSG_UPGRADE: // 弹出对话框，询问是否升级
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
                        try {
                            autoService.upgrade();
                        } catch (RemoteException e) {
                            //
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void handleMessage(int msgID, String data) {
        Message msg = new Message();
        msg.what = msgID;
        Bundle bundle = new Bundle();
        bundle.putString(MSG_KEY, data);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    /**
     * SSL广播接收器
     * 为防止接收器的阻塞，最好将耗时的操作放入handle中完成
     */
    private class SSLReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra(ACTION_INTENT_DATA);
            final String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_INTENT_STARTSERVER_INPROC:
                        handleMessage(MSG_SHOW_LOG, data); // 正在启动服务
                        break;
                    case ACTION_INTENT_STARTSERVER_SUCCESS:
                        handleMessage(MSG_SHOW_LOG, "启动服务成功！");
                        break;
                    case ACTION_INTENT_STARTSERVER_FAILURE:
                        handleMessage(MSG_SHOW_LOG, "启动服务失败！");
                        break;
                    case ACTION_INTENT_DOWNLOADCFG_SUCCESS:
                        handleMessage(MSG_SHOW_LOG, "下载策略成功！");
                        break;
                    case ACTION_INTENT_STOPSERVER_SUCCESS:
                        handleMessage(MSG_SHOW_LOG, "停止服务成功！");
                        break;
                    case ACTION_INTENT_UPGRADE:
                        handleMessage(MSG_UPGRADE, data); // 升级应用
                        break;
                    case ACTION_INTENT_NETWORK_CONNECTED:
                        handleMessage(MSG_SHOW_LOG, "网络已链接");
                        break;
                    case ACTION_INTENT_NETWORK_DISCONNECTED:
                        handleMessage(MSG_SHOW_LOG, "网络已断开");
                        break;
                    case ACTION_INTENT_CHECKAPPS_SUCCESS:
                        handleMessage(MSG_SHOW_LOG, "后台应用服务检测成功：" + data);
                        break;
                    case ACTION_INTENT_CHECKAPPS_FAILURE:
                        handleMessage(MSG_SHOW_LOG, "后台应用服务检测失败：" + data);
                        break;
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        handler = new MyHandler(this);
        registerReceiver();
        registerService();
    }

    private void initView() {
        setContentView(R.layout.main);
        findViewById(R.id.btnStart).setOnClickListener(this);
        findViewById(R.id.btnStop).setOnClickListener(this);
        findViewById(R.id.btnApps).setOnClickListener(this);
        findViewById(R.id.btnCertInfo).setOnClickListener(this);
        findViewById(R.id.btnInterfaceExtend).setOnClickListener(this);
        findViewById(R.id.btnQuit).setOnClickListener(this);
        txtLog = (TextView) findViewById(R.id.txtLog);
        sclView = (ScrollView) findViewById(R.id.sclView);
    }

    // 绑定SSL服务
    private void registerService() {
        Intent intent = new Intent(KOAL_SERVICE);
        startService(intent);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                autoService = IAutoService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                autoService = null;
            }
        };
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    // 广播接收器，用来监听SSL服务发出的广播
    private void registerReceiver() {
        srvMonitor = new SSLReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_STARTSERVER_INPROC);
        filter.addAction(ACTION_INTENT_STARTSERVER_SUCCESS);
        filter.addAction(ACTION_INTENT_STARTSERVER_FAILURE);
        filter.addAction(ACTION_INTENT_DOWNLOADCFG_SUCCESS);
        filter.addAction(ACTION_INTENT_STOPSERVER_SUCCESS);
        filter.addAction(ACTION_INTENT_UPGRADE);
        filter.addAction(ACTION_INTENT_NETWORK_CONNECTED);
        filter.addAction(ACTION_INTENT_NETWORK_DISCONNECTED);
        filter.addAction(ACTION_INTENT_CHECKAPPS_SUCCESS);
        filter.addAction(ACTION_INTENT_CHECKAPPS_FAILURE);
        registerReceiver(srvMonitor, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消绑定SSL服务
        unregisterReceiver(srvMonitor);
        unbindService(serviceConnection);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnStart:
                    if (!autoService.isStarted()) {
                        // autoService.setTFModel("F-model");
                        // autoService.setCurCert("filecert.pfx");
                        // autoService.setPin("111111", true);
                        autoService.start();
                    }
                    break;

                case R.id.btnStop:
                    autoService.stop();
                    break;

                case R.id.btnApps:
                    appendLog(autoService.getApps());
                    break;

                case R.id.btnCertInfo:
                    appendLog("SN:" + autoService.getCertInfo(1));
                    appendLog("CN:" + autoService.getCertInfo(2));
                    appendLog("DN:" + autoService.getCertInfo(3));
                    appendLog("SHECA:" + autoService.getCertInfo(4));
                    break;

                // 此处为扩展接口的使用，具体使用请参考文档。
                case R.id.btnInterfaceExtend:
                    String[] config = {
                            "server_addr",
                            "server_type", "server_port", "server_addr1",
                            "server_port1", "server_type1", "server_addr2",
                            "server_port2", "server_type2", "server_addr3",
                            "server_port3", "server_type3", "server_addr4",
                            "server_port4", "server_type4", "connect_timeout",
                            "device", "cert", "rapid_access", "save_pin", "auto_policy",
                            "policy_only_http", "auto_proxy", "tunnel", "check_app", "check_app_interval",
                            "display_notify", "display_toast", "notify_netstat", "notify_appstat", "debug", "log",
                            "tun_log", "auto_start", "ui_theme"};
                    String[] devInfo = {
                            "DEV_PHONE_IMEI",
                            "DEV_PHONE_IMSI",
                            "DEV_TF_MODEL",
                            "DEV_TF_ID",
                            "DEV_TF_CERT"};
                    String[] serviceInfo = {
                            "SERVICE_VPN_STATE",
                            "SERVICE_PROXY_STATE",
                            "SERVICE_TUN_IP",
                            "SERVICE_TUN_NETMASK",
                            "SERVICE_TUN_MTU",
                            "SERVICE_TUN_DNS",
                            "SERVICE_TUN_ROUTES",
                            "SERVICE_PROXY_LOG",
                            "SERVICE_TUN_LOG",
                            "SERVICE_VPN_CONF_PROTO",
                            "SERVICE_VPN_CONF_REMOTE",
                            "SERVICE_VPN_CONF_RPORT"};

                    appendLog("certBase64:" + autoService.getCertInfo(0));
                    for (int i = 0; i < config.length; i++) {
                        appendLog(config[i] + ":" + autoService.getConfig(config[i]));
                    }
                    for (int i = 0; i < devInfo.length; i++) {
                        appendLog(devInfo[i] + ":" + autoService.getDevInfo(devInfo[i]));
                    }
                    for (int i = 0; i < serviceInfo.length; i++) {
                        appendLog(serviceInfo[i] + ":" + autoService.getServiceInfo(serviceInfo[i]));
                    }
                    break;
                case R.id.btnQuit:
                    autoService.quit();
                    finish();
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void appendLog(String strLog) {
        txtLog.append(strLog + "\n");
        // 自动滚屏
        sclView.post(new Runnable() {
            @Override
            public void run() {
                sclView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}

