package com.lee.wechatdemo;

import android.app.Application;
import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description
 */
public class ShareApp extends Application {

    private static IWXAPI mWxapi;

    public static IWXAPI getmWxapi() {
        return mWxapi;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mWxapi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        mWxapi.registerApp(Constants.APP_ID);
    }
}
