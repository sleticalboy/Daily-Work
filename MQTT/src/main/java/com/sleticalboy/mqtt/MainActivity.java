package com.sleticalboy.mqtt;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.sleticalboy.mqtt.bean.user.LoginResponse;
import com.sleticalboy.mqtt.push.AMqttClient;
import com.sleticalboy.mqtt.push.MqttClientConfig;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {
    
    // httpUri: >https://ccb.dehuinet.cn
    // clientId: 17401
    // pwd: 3402538
    // pushUri: ssl://test.dehuinet.com:1830
    // ca 证书
    // topics: /u/2A-3Kj9rdp26i0RivrsMlcLBScw=
    private static final String TAG = "MainActivity";
    private MqttClientConfig mqttConfig;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent();
        try {
            AMqttClient.getInstance().init(this, mqttConfig);
            AMqttClient.getInstance().connect(this);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }
    
    private void handleIntent() {
        final LoginResponse response = (LoginResponse)
                getIntent().getSerializableExtra("login_response");
        mqttConfig = new MqttClientConfig.Builder()
                .pushUri(HttpConfig.sPushUrl)
                .clientId(response.getUserInfo().getPushClientId())
                .password(response.getMqttPassword())
                .topics(new String[]{response.getUserInfo().getPushChannelId()})
                .ops(new int[]{0})
                .caEnable(true)
                .caInputStream(getResources().openRawResource(R.raw.mqtt))
                .build();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            AMqttClient.getInstance().disconnect(this);
            AMqttClient.getInstance().destroy(this);
        } catch (MqttException e) {
            Log.e(TAG, "onDestroy: ", e);
        }
    }
}
