package com.sleticalboy.dailywork.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.http.RetrofitClient;
import com.sleticalboy.dailywork.http.api.LiveAPIService;
import com.sleticalboy.dailywork.util.MResource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private static final int MSG_ERROR = 727;
    private static final int MSG_GET_ID = 195;
    private ScrollView svResult;
    private TextView tvResult;

    private LiveAPIService mLiveAPIService;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_QUERY:
                    tvResult.append("query = " + msg.obj + "\n");
                    break;
                case MSG_REGISTER:
                    tvResult.append("register = " + msg.obj + "\n");
                    break;
                case MSG_JUDGE:
                    tvResult.append("judge = " + msg.obj + "\n");
                    break;
                case MSG_ERROR:
                    tvResult.append("error = " + msg.obj + "\n");
                    break;
                case MSG_GET_ID:
                    tvResult.append("id = " + msg.obj + "\n");
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
        findViewById(R.id.btnGetIdByName).setOnClickListener(this);
        svResult = (ScrollView) findViewById(R.id.svResult);
        tvResult = (TextView) findViewById(R.id.tvResult);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void prepareWork() {
        mLiveAPIService = RetrofitClient.getInstance().create(LiveAPIService.class);
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
            case R.id.btnGetIdByName:
                getIdByName();
                break;
        }
    }

    private void getIdByName() {
        final int idByName = MResource.getIdByName(this, "layout", "activity_classify");
        final Message msg = Message.obtain();
        msg.obj = idByName;
        msg.what = MSG_GET_ID;
        mHandler.sendMessage(msg);
    }

    private void judge() {
        final Map<String, Object> params = new HashMap<>();
        params.put("BolgTp", "1");
        params.put("ThrshIdVal", "asdf");
        params.put("EqmtTp", "1");
        params.put("CstNo", "t11");
        params.put("ImgFileNm", "asdf");
//        final String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//        final File file = new File(sdCardPath + "/libin-blue.jpg");
        final File file = new File("/sdcard/libin-blue.jpg");
        RequestBody imageRequestBody = MultipartBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part img = MultipartBody.Part.createFormData("file", file.getName(), imageRequestBody);
        mLiveAPIService.judge(params, img).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                final Message msg = Message.obtain();
                msg.obj = result;
                msg.what = MSG_JUDGE;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                final Message msg = Message.obtain();
                msg.obj = t.getMessage();
                msg.what = MSG_ERROR;
                mHandler.sendMessage(msg);
            }
        });
    }

    private void register() {
        final File file = new File("/sdcard/libin-blue.jpg");
        final Map<String, Object> params = new HashMap<>();
        params.put("BolgTp", 1);
        params.put("EqmtTp", "1");
        params.put("UsrNm", "t10"); // 登录名
        params.put("IdentTp", "10100");
        params.put("CstNo", "t11"); // 工号或者身份证号码
        params.put("InstCd", ""); // 部门名称
        params.put("NtnCd", "神魔族");
        params.put("ImgFileNm", file.getAbsolutePath());
        params.put("GndTp", "1"); //
        params.put("BnkCrdNo", "6214680100622057"); // 银行卡号码
        RequestBody imageRequestBody = MultipartBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part img = MultipartBody.Part.createFormData("file", file.getName(), imageRequestBody);
        mLiveAPIService.register(params, img).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                final Message msg = Message.obtain();
                msg.obj = result;
                msg.what = MSG_REGISTER;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                final Message msg = Message.obtain();
                msg.obj = t.getMessage();
                msg.what = MSG_ERROR;
                mHandler.sendMessage(msg);
            }
        });
    }

    private void query() {
        final Map<String, Object> params = new HashMap<>();
        params.put("BolgTp", 1);
        params.put("CstNo", "t11");
        mLiveAPIService.query(params).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                final Message msg = Message.obtain();
                msg.obj = response.body();
                msg.what = MSG_QUERY;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (!call.isCanceled()) {
                    call.cancel();
                }
                final Message msg = Message.obtain();
                msg.obj = t.getMessage();
                msg.what = MSG_ERROR;
                mHandler.sendMessage(msg);
            }
        });
    }
}
