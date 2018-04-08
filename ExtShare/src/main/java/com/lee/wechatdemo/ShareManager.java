package com.lee.wechatdemo;

import com.lee.wechatdemo.manager.WXShareManager;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description
 */
public class ShareManager {

    public void shareText2WeChat(String text) {
        WXShareManager.shareText(text);
    }

    public void shareImage2WeChat(String imageUrl) {
        WXShareManager.shareImage(imageUrl);
    }

    public void sharePage2WeChat(String pageUrl, String title, String thumbUrl, String description) {
        WXShareManager.sharePageUrl(pageUrl, title, thumbUrl, description);
    }

//    public static void shareText2SinaWeibo(String text) {
//        WBShareManager.shareText(text);
//    }
}
