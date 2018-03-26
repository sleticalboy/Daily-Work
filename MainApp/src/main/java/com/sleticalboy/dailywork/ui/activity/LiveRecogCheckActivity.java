package com.sleticalboy.dailywork.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.http.RetrofitClient;
import com.sleticalboy.dailywork.http.api.LiveRecogAPI;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 18-3-26.
 *
 * @author sleticalboy
 * @description
 */
public class LiveRecogCheckActivity extends BaseActivity implements View.OnClickListener {

    private static final int MSG_QUERY = 262;
    private static final int MSG_REGISTER = 98;
    private static final int MSG_JUDGE = 302;
    private ScrollView svResult;
    private TextView tvResult;

    private LiveRecogAPI liveRecogAPI;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_QUERY:
                    tvResult.setText("query = " + msg.obj);
                    break;
                case MSG_REGISTER:
                    tvResult.setText("register = " + msg.obj);
                    break;
                case MSG_JUDGE:
                    tvResult.setText("judge = " + msg.obj);
                    break;
            }
            new Handler().post(() -> svResult.fullScroll(ScrollView.FOCUS_DOWN));
        }
    };

    @Override
    protected int attachLayout() {
        return R.layout.activity_live_recog;
    }

    @Override
    protected void initView() {
        findViewById(R.id.btnQuery).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);
        findViewById(R.id.btnJudge).setOnClickListener(this);
        svResult = (ScrollView) findViewById(R.id.svResult);
        tvResult = (TextView) findViewById(R.id.tvResult);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void prepareWork() {
        liveRecogAPI = RetrofitClient.getInstance().create(LiveRecogAPI.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnQuery:
                query();
                break;
            case R.id.btnRegister:
                register();
                break;
            case R.id.btnJudge:
                judge();
                break;
        }
    }

    private void judge() {
        /*
        * params.add(new BasicNameValuePair("BolgTp", "1"));
        params.add(new BasicNameValuePair("ThrshIdVal", ""));
        params.add(new BasicNameValuePair("EqmtTp", "1"));
        params.add(new BasicNameValuePair("CstNo", "t1"));
        params.add(new BasicNameValuePair("ImgFileNm", file.getAbsolutePath()));*/
        final Map<String, String> params = new HashMap<>();
        params.put("BolgTp", "1");
        params.put("ThrshIdVal", "asdf");
        params.put("EqmtTp", "1");
        params.put("CstNo", "t1");
        params.put("ImgFileNm", "");
        Runnable runnable = () -> {
            String result = liveRecogAPI.judge(params);
            final Message msg = Message.obtain();
            msg.obj = result;
            msg.what = MSG_JUDGE;
            mHandler.sendMessage(msg);
        };
        new Thread(runnable).start();
    }

    private void register() {
        final Map<String, String> params = new HashMap<>();
        Runnable runnable = () -> {
            String result = liveRecogAPI.register(params);
            final Message msg = Message.obtain();
            msg.obj = result;
            msg.what = MSG_REGISTER;
            mHandler.sendMessage(msg);
        };
        new Thread(runnable).start();
    }

    private void query() {
        final Map<String, String> params = new HashMap<>();
        params.put("BolgTp", "1");
        params.put("CstNo", "1");
        Runnable runnable = () -> {
            String result = liveRecogAPI.query(params);
            final Message msg = Message.obtain();
            msg.obj = result;
            msg.what = MSG_QUERY;
            mHandler.sendMessage(msg);
        };
        new Thread(runnable).start();
    }
}
