package com.sleticalboy.mqtt.push;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created on 18-7-22.
 *
 * @author leebin
 */
public class AMqttClient {

    private static final String TAG = "AMqttClient";

    private static AMqttClient sClient;
    private MqttAsyncClient mClient;
    private MqttClientConfig mConfig;

    private AMqttClient() {
    }

    public static AMqttClient getInstance() {
        if (sClient == null) {
            synchronized (AMqttClient.class) {
                if (sClient == null) {
                    sClient = new AMqttClient();
                }
            }
        }
        return sClient;
    }

    public void init(Context context, MqttClientConfig config) throws MqttException {
        if (config == null) {
            return;
        }
        mConfig = config;
        final String pushUri = mConfig.getPushUri();
        final String clientId = mConfig.getClientId();
        if (TextUtils.isEmpty(pushUri) || TextUtils.isEmpty(clientId)) {
            return;
        }
        mClient = new MqttAsyncClient(pushUri, clientId, new MemoryPersistence());
        mClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connectionLost() called with: cause = [" + cause + "]");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "messageArrived() called with: topic = [" + topic + "], message = [" + message + "]");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "deliveryComplete() called with: token = [" + token + "]");
            }
        });
    }

    public void connect(Context context) throws Exception {
        if (isConnected()) {
            return;
        }
        final MqttConnectOptions options = MqttClientConfig.create(mConfig);
        mClient.connect(options, null, new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken asyncActionToken) {
                Log.d(TAG, "onSuccess() called with: asyncActionToken = [" + asyncActionToken + "]");
                try {
                    subscribe(context);
                } catch (MqttException e) {
                    Log.e(TAG, "onConnectSuccess: ", e);
                }
            }

            @Override
            public void onFailure(final IMqttToken asyncActionToken, final Throwable thr) {
                Log.d(TAG, "onConnectFailure() called with: asyncActionToken = [" + asyncActionToken + "], exception = [" + thr + "]");
                if (thr instanceof MqttException) {
                    //
                } else {
                    // unknown exception
                }
            }
        });
    }

    private void subscribe(Context context) throws MqttException {
        if (!isConnected()) {
            return;
        }
        mClient.subscribe(mConfig.getTopics(), mConfig.getOps(), null, new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken asyncActionToken) {
                Log.d(TAG, "onSubscribeSuccess() called with: asyncActionToken = [" + asyncActionToken + "]");
            }

            @Override
            public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                Log.d(TAG, "onSubscribeFailure() called with: asyncActionToken = [" + asyncActionToken + "], exception = [" + exception + "]");
            }
        });
    }

    public void disconnect(Context context) throws MqttException {
        if (isConnected()) {
            return;
        }
        mClient.disconnect(null, new IMqttActionListener() {
            @Override
            public void onSuccess(final IMqttToken asyncActionToken) {
                Log.d(TAG, "onDisconnectSuccess() called with: asyncActionToken = [" + asyncActionToken + "]");
            }

            @Override
            public void onFailure(final IMqttToken asyncActionToken, final Throwable exception) {
                Log.d(TAG, "onDisconnectFailure() called with: asyncActionToken = [" + asyncActionToken + "], exception = [" + exception + "]");
            }
        });
    }

    public void destroy(Context context) throws MqttException {
        if (isConnected()) {
            mClient.close();
            mClient = null;
        }
    }

    private boolean isConnected() {
        return mClient != null && mClient.isConnected();
    }
}
