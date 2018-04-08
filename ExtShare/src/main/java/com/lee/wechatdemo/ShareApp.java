package com.lee.wechatdemo;

import android.app.Application;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description
 */
public class ShareApp extends Application {

    private static final java.lang.String APP_ID = "wx24696ce81c4cd191";
    private static IWXAPI mWxapi;

    @Override
    public void onCreate() {
        super.onCreate();
        mWxapi = WXAPIFactory.createWXAPI(this, APP_ID, true);
        mWxapi.registerApp(APP_ID);
    }

    public static IWXAPI getmWxapi() {
        return mWxapi;
    }
}
