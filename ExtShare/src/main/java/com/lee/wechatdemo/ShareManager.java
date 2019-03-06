package com.lee.wechatdemo;

import android.content.Context;

import com.lee.wechatdemo.manager.WBShareManager;
import com.lee.wechatdemo.manager.WXShareManager;
import com.sina.weibo.sdk.auth.AuthInfo;

/**
 * Created on 18-4-8.
 *
 * @author leebin
 * @description share manager for external share
 */
public class ShareManager {

    private ShareManager() {
    }

    public static ShareManager newInstance() {
        return Holder.MANAGER;
    }

    public void initWeChatSDK(Context context, String appId) {
        WXShareManager.init(context, appId);
    }

    public void initSinaWeiboSDK(Context context, AuthInfo authInfo) {
        WBShareManager.init(context, authInfo);
    }

    public void shareText2WeChat(String text) {
        WXShareManager.shareText(text);
    }

    public void shareImage2WeChat(String imageUrl) {
        WXShareManager.shareImage(imageUrl);
    }

    public void sharePage2WeChat(String pageUrl, String title, String thumbUrl, String description) {
        WXShareManager.sharePageUrl(pageUrl, title, thumbUrl, description);
    }

    public void shareText2SinaWeibo(String text) {
       WBShareManager.shareText(text);
    }

    private static final class Holder {
        private static final ShareManager MANAGER = new ShareManager();
    }

}
