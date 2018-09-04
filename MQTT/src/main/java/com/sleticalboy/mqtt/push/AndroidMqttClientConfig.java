package com.sleticalboy.mqtt.push;

/**
 * Created on 18-7-22.
 *
 * @author sleticalboy
 * @description
 */
public class AndroidMqttClientConfig {

    private String mPushServerUri;
    private String mClientId;

    public String getPushServerUri() {
        return mPushServerUri;
    }

    public void setPushServerUri(String pushServerUri) {
        mPushServerUri = pushServerUri;
    }

    public String getClientId() {
        return mClientId;
    }

    public void setClientId(String clientId) {
        mClientId = clientId;
    }
}
