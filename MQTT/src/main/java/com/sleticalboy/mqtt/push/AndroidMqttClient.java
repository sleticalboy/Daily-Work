package com.sleticalboy.mqtt.push;

import android.content.Context;
import android.text.TextUtils;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created on 18-7-22.
 *
 * @author sleticalboy
 * @description
 */
public class AndroidMqttClient {

    private static AndroidMqttClient sClient;
    private MqttClient mClient;
    private Context mContext;

    public interface MqttCloseListener {

        void onSuccess();

        void onFailure(Throwable t);
    }

    private AndroidMqttClient() {
    }

    public static AndroidMqttClient getInstance() {
        if (sClient == null) {
            synchronized (AndroidMqttClient.class) {
                if (sClient == null) {
                    sClient = new AndroidMqttClient();
                }
            }
        }
        return sClient;
    }

    private void init(Context context, AndroidMqttClientConfig config) throws MqttException {
        mContext = context;
        initClient(config.getPushServerUri(), config.getClientId());
    }

    private void initClient(String pushServerUri, String clientId) throws MqttException {
        if (TextUtils.isEmpty(pushServerUri) || pushServerUri.trim().length() == 0
                || TextUtils.isEmpty(clientId) || clientId.trim().length() == 0) {
            return;
        }
        mClient = new MqttClient(pushServerUri, clientId, new MemoryPersistence());
        mClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
