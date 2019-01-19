package com.lee.wechatdemo.wxapi;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.lee.wechatdemo.ShareApp;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * Created on 18-4-8.
 *
 * @author sleticalboy
 * @description 分享到微信结果回调入口类
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShareApp.getmWxapi().handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseRequest) {
        // 处理回调结果
    }

    @Override
    public void onResp(BaseResp baseResponse) {

    }
}
