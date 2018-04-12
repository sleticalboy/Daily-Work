package com.lee.wechatdemo.manager;

import android.content.Context;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description Sina Weibo share manager
 */
public class WBShareManager {

    public static void shareText(String text) {
        //
    }

    public static void init(Context context, AuthInfo authInfo) {
        WbSdk.install(context.getApplicationContext(), authInfo);
    }
}
