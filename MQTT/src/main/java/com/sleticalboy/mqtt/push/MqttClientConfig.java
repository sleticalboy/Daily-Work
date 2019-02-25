package com.sleticalboy.mqtt.push;

import com.sleticalboy.mqtt.ssl.AX509TrustManager;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.io.InputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created on 18-7-22.
 *
 * @author leebin
 */
public final class MqttClientConfig implements Serializable {

    private static final long serialVersionUID = -212402032877656612L;
    private String pushUri;
    private String clientId;
    private String password;
    private boolean isCaEnable;
    private InputStream caInputStream;
    private String[] topics;
    private int[] ops;

    private MqttClientConfig() {
    }

    public String getPushUri() {
        return pushUri;
    }

    public String getClientId() {
        return clientId;
    }

    public String[] getTopics() {
        return topics;
    }

    public int[] getOps() {
        return ops;
    }

    public static final class Builder {
        private String pushUri;
        private String clientId;
        private String password;
        private boolean isCaEnable;
        private InputStream caInputStream;
        private String[] topics;
        private int[] ops;

        public Builder pushUri(String pushUri) {
            this.pushUri = pushUri;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder topics(String[] topics) {
            this.topics = topics;
            return this;
        }

        public Builder ops(int[] ops) {
            this.ops = ops;
            return this;
        }

        public Builder caEnable(boolean isCaEnable) {
            this.isCaEnable = isCaEnable;
            return this;
        }

        public Builder caInputStream(InputStream caInputStream) {
            this.caInputStream = caInputStream;
            return this;
        }

        public MqttClientConfig build() {
            final MqttClientConfig config = new MqttClientConfig();
            config.pushUri = pushUri;
            config.clientId = clientId;
            config.password = password;
            config.topics = topics;
            config.ops = ops;
            config.isCaEnable = isCaEnable;
            config.caInputStream = caInputStream;
            return config;
        }
    }

    public static MqttConnectOptions create(MqttClientConfig config) throws Exception {
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName("mobile");
        options.setAutomaticReconnect(true);
        options.setPassword(config.password.toCharArray());
        final TrustManager[] tm;
        if (config.isCaEnable) {
            tm = new TrustManager[]{new AX509TrustManager()};
        } else {
            try {
                final CertificateFactory factory = CertificateFactory.getInstance("X.509");
                final Certificate ca = factory.generateCertificate(config.caInputStream);
                final KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", ca);
                final TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(keyStore);
                tm = tmf.getTrustManagers();
            } finally {
                config.caInputStream.close();
            }
        }
        final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, tm, null);
        options.setSocketFactory(sslContext.getSocketFactory());
        return options;
    }
}
