package com.sleticalboy.mqtt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sleticalboy.http.HttpClient;
import com.sleticalboy.http.builder.PostBuilder;
import com.sleticalboy.http.builder.RequestBuilder;
import com.sleticalboy.http.callback.HttpCallback;
import com.sleticalboy.mqtt.bean.user.LoginResponse;
import com.sleticalboy.mqtt.util.CommonUtils;

/**
 * Created on 19-2-23.
 *
 * @author leebin
 */
public class LoginActivity extends AppCompatActivity {
    
    private static final String TAG = "LoginActivity";

    // login_name=t101
    // password=ea7fb08b0a2737e402509c9aa869bd16
    // grant_type=password
    // nonce=l8lxuCdxhOOkeyNrRnIbYQ==
    // client_id=2
    // include_user=true
    // device_uuid=1e76a7d297275125c0b56d8188943217ad4fa90c
    // mqtt=true
    /*
    params:
    0 = {BasicNameValuePair@7386} "grant_type=password"
    1 = {BasicNameValuePair@7387} "login_name=t101"
    2 = {BasicNameValuePair@7388} "nonce=ueXcMMSjPFxlAYID2ndw/A=="
    3 = {BasicNameValuePair@7389} "password=7dad9a886ac16bfa009825426a2ad321"
    4 = {BasicNameValuePair@7390} "client_id=2"
    5 = {BasicNameValuePair@7391} "include_user=true"
    6 = {BasicNameValuePair@7392} "device_uuid=1e76a7d297275125c0b56d8188943217ad4fa90c"
    7 = {BasicNameValuePair@7393} "mqtt=true"
    headers:
    0 = {TreeMap$TreeMapEntry@7376} "Content-Type" -> "application/x-www-form-urlencoded"
    1 = {TreeMap$TreeMapEntry@7377} "X-CLIENT-CHECKSUM" -> "80485b481c64efa621e7d66c26153d4b44e8f91f"
    1 = {TreeMap$TreeMapEntry@7377} "User-Agent" -> "MinxingMessenger/1.0 (samsung-SM-G9550; Android/8.0.0)"
    httpParams:
    0 = {HashMap$Node@7419} "http.protocol.content-charset" -> "UTF-8"
    1 = {HashMap$Node@7420} "http.socket.buffer-size" -> "8192"
    2 = {HashMap$Node@7421} "http.protocol.handle-redirects" -> "true"
    3 = {HashMap$Node@7422} "http.protocol.version" -> "HTTP/1.1"
    4 = {HashMap$Node@7423} "http.socket.timeout" -> "60000"
    5 = {HashMap$Node@7424} "http.connection.timeout" -> "60000"
    6 = {HashMap$Node@7425} "http.useragent" -> "MinxingMessenger/1.0 (samsung-SM-G9550; Android/8.0.0)"
     */
    public static final String OAUTH = "/oauth2/token";
    public static final String SKIN_CHANGE = "/api/v2/change/skin";

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String nonce = CommonUtils.getRawKey();
        final String username = "t101";
        final String password = CommonUtils.encrypt(nonce, "111111");
        final String uuid = CommonUtils.getUuid(this);
        final String checkSum = CommonUtils.clientCheckSum(RequestBuilder.BASE_URL, username);
        final String userAgent = CommonUtils.userAgent(this);
        final RequestBuilder builder = new PostBuilder()
                .url(OAUTH)
                .param("login_name", username)
                .param("grant_type", "password")
                .param("nonce", nonce)
                .param("password", password)
                .param("client_id", "1")
                .param("include_user", "true")
                .param("device_uuid", uuid)
                .param("mqtt", "true")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-CLIENT-CHECKSUM", checkSum)
                .header("User-Agent", userAgent);
        // final RequestBuilder builder = new GetBuilder().url(SKIN_CHANGE);
        HttpClient.getInstance().request(builder, new HttpCallback<LoginResponse>() {
            @Override
            public void onSuccess(final LoginResponse response) {
                final Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("login_response", response);
                startActivity(intent);
            }
    
            @Override
            public void onFailure(final Throwable e) {
                Log.e(TAG, "onFailure: ", e);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // finish();
            }
        });
    }
}
