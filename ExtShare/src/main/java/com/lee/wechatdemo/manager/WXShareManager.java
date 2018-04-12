package com.lee.wechatdemo.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lee.wechatdemo.ShareApp;
import com.lee.wechatdemo.util.Util;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description WeChat share manager
 */
public class WXShareManager {

    private static IWXAPI sWxapi;

    public static void init(Context context, String appId) {
        sWxapi = WXAPIFactory.createWXAPI(context, appId, false);
        sWxapi.registerApp(appId);
    }

    public static void shareText(String text) {
        WXTextObject textObject = new WXTextObject(text);
        if (textObject.checkArgs()) {
            SendMessageToWX.Req request = new SendMessageToWX.Req();
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.message = new WXMediaMessage(textObject);
            sWxapi.sendReq(request);
        }
    }

    public static void shareImage(String imageUrl) {
        WXImageObject imageObject = new WXImageObject();
        imageObject.imagePath = imageUrl;
        imageObject.imageData = getBytes(imageUrl);
        if (imageObject.checkArgs()) {
            WXMediaMessage message = new WXMediaMessage();
            message.mediaObject = imageObject;
            final SendMessageToWX.Req request = new SendMessageToWX.Req();
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.message = message;
            sWxapi.sendReq(request);
        }
    }

    private static byte[] getBytes(String imageUrl) {
        final byte[] imageData;
        if (imageUrl.startsWith("http")) {
            imageData = Util.getHtmlByteArray(imageUrl);
        } else {
            final Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
            imageData = Util.bmpToByteArray(bitmap, true);
        }
        return imageData;
    }

    public static void sharePageUrl(String pageUrl, String title, String thumbUrl, String description) {
        WXWebpageObject webpageObject = new WXWebpageObject(pageUrl);
        if (webpageObject.checkArgs()) {
            WXMediaMessage message = new WXMediaMessage();
            message.mediaObject = webpageObject;
            message.title = title;
            message.thumbData = getBytes(thumbUrl);
            message.description = description;
            final SendMessageToWX.Req request = new SendMessageToWX.Req();
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.message = message;
            sWxapi.sendReq(request);
        }
    }
}
