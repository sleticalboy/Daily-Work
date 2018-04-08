package com.lee.wechatdemo;

import com.lee.wechatdemo.manager.WXShareManager;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description share manager for external share
 */
public class ShareManager {

    private ShareManager() {
    }

    public static ShareManager newInstance() {
        return Holder.MANAGER;
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

    private static final class Holder {
        private static final ShareManager MANAGER = new ShareManager();
    }

}
