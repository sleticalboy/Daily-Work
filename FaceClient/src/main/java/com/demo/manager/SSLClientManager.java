package com.demo.manager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import koal.ssl.IAutoService;

/**
 * Created on 18-2-27.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public final class SSLClientManager {

    private static final String TAG = "SSLClientManager";
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

    // 配置信息
    public static final String[] CONFIG = {
            "server_addr", "server_type", "server_port",
            "server_addr1", "server_port1", "server_type1",
            "server_addr2", "server_port2", "server_type2",
            "server_addr3", "server_port3", "server_type3",
            "server_addr4", "server_port4", "server_type4",
            "connect_timeout", "device", "cert", "rapid_access", "save_pin", "auto_policy",
            "policy_only_http", "auto_proxy", "tunnel", "check_app", "check_app_interval",
            "display_notify", "display_toast",
            "notify_netstat", "notify_appstat",
            "debug", "log", "tun_log", "auto_start", "ui_theme"
    };
    // 设备信息
    public static final String[] DEV_INFO = {
            "DEV_PHONE_IMEI", "DEV_PHONE_IMSI", "DEV_TF_MODEL", "DEV_TF_ID", "DEV_TF_CERT"};
    // 服务信息
    public static final String[] SERVICE_INFO = {
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
            "SERVICE_VPN_CONF_RPORT"
    };

    public static final String MSG_KEY = "data";
    public static final int MSG_SHOW_LOG = 1;
    public static final int MSG_UPGRADE = 2;
    private static final SSLClientManager CLIENT_MANAGER = new SSLClientManager();

    private SSLReceiver srvMonitor;
    private ServiceConnection serviceConnection;
    private IAutoService autoService;
    private Handler mHandler;

    public static SSLClientManager getInstance() {
        synchronized (CLIENT_MANAGER) {
            return CLIENT_MANAGER;
        }
    }

    public void setHandler(Handler handler) {
        if (handler == null) {
            throw new NullPointerException("Handler is null");
        }
        mHandler = handler;
    }

    private void startServer(Context context) {
        Intent intent = new Intent(KOAL_SERVICE);
        intent.setPackage(context.getPackageName());
        context.startService(intent);
        appendLog("start 服务， intent = " + intent);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                autoService = IAutoService.Stub.asInterface(service);
                appendLog("启动服务， autoService" + autoService);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                autoService = null;
                appendLog("关闭服务， autoService = " + null);
            }
        };
        context.bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private void registerReceiver(Context context) {
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
        context.registerReceiver(srvMonitor, filter);
        appendLog("注册广播， srvMonitor = " + srvMonitor);
    }

    private void handleMessage(int msgID, String data) {
        Message msg = Message.obtain(mHandler);
        msg.what = msgID;
        Bundle bundle = new Bundle();
        bundle.putString(MSG_KEY, data);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private void appendLog(String log) {
        Log.d(TAG, log);
    }

    private void unregisterReceiver(Context context) {
        context.unregisterReceiver(srvMonitor);
    }

    private void stopServer(Context context) {
        context.unbindService(serviceConnection);
    }

    public boolean checkRemoteService() {
        return autoService != null;
    }

    public void start(Context context) {
        registerReceiver(context);
        startServer(context);
    }

    public void destroy(Context context) {
        unregisterReceiver(context);
        stopServer(context);
    }

    public void upgrade() {
        try {
            autoService.upgrade();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            autoService.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getApps() {
        try {
            return autoService.getApps();
        } catch (RemoteException e) {
            return null;
        }
    }

    public void quit() {
        try {
            autoService.quit();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getCertInfo(int option) {
        try {
            return autoService.getCertInfo(option);
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getConfig(String key) {
        try {
            return autoService.getConfig(key);
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getDevInfo(String key) {
        try {
            return autoService.getDevInfo(key);
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getServiceInfo(String key) {
        try {
            return autoService.getServiceInfo(key);
        } catch (RemoteException e) {
            return null;
        }
    }

    public boolean isStarted() {
        try {
            return checkRemoteService() && autoService.isStarted();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void restart() {
        try {
            autoService.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setTFModel(String tfModel) {
        try {
            autoService.setTFModel(tfModel);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setCurCert(String certFilePath) {
        try {
            autoService.setCurCert(certFilePath);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setPin(String pin, boolean save) {
        try {
            autoService.setPin(pin, save);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * SSL广播接收器
     * 为防止接收器的阻塞，最好将耗时的操作放入handler中完成
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
}
