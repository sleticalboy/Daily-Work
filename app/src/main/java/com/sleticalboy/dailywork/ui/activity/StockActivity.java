package com.sleticalboy.dailywork.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.bean.Stock;
import com.sleticalboy.dailywork.http.HttpUtils;

import java.io.IOException;
import java.text.DecimalFormat;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StockActivity extends BaseActivity {

    @Override
    protected void initData() {
        String url = "https://gupiao.baidu.com/api/rails/stockbasicbatch?format=json&stock_code=hk02799";
        HttpUtils.request(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(StockActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody vBody = response.body();
                if (vBody == null) {
                    return;
                }
                String vJson = vBody.string();
                if (vJson == null) {
                    return;
                }
                final Stock vStock = new Gson().fromJson(vJson, Stock.class);
                if (vStock.data != null && vStock.data.size() > 0) {
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            setStockView(vStock.data.get(0));
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int attachLayoutId() {
        return R.layout.activity_main;
    }

    private void setStockView(Stock.DataEntity stock) {
        DecimalFormat decimalFormat = new DecimalFormat("0.000");

        TextView tv_stock_close = (TextView) findViewById(R.id.tv_stock_close);
        tv_stock_close.setText(String.format("%s↑", decimalFormat.format(stock.close)));

        TextView stockNetchange = (TextView) findViewById(R.id.tv_stock_netchange);
        stockNetchange.setText(String.format("+%s", decimalFormat.format(stock.netChange)));

        TextView stockNetchangeRatio = (TextView) findViewById(R.id.tv_stock_netchange_ratio);
        stockNetchangeRatio.setText(String.format("+%s%%", decimalFormat.format(stock.netChangeRatio)));

        // 昨收
        TextView stockPreclose = (TextView) findViewById(R.id.tv_stock_preclose);
        stockPreclose.setText(String.format("昨收 %s", decimalFormat.format(stock.preClose)));

        TextView stockOpen = (TextView) findViewById(R.id.tv_stock_open);
        SpannableString openString = new SpannableString(String.format("今开 %s", decimalFormat.format(stock.open)));
        stockOpen.setText(setSpans(openString, "#FF4B4F"));

        TextView stockHigh = (TextView) findViewById(R.id.tv_stock_high);
        SpannableString highString = new SpannableString(String.format("最高 %s", decimalFormat.format(stock.high)));
        stockHigh.setText(setSpans(highString, "#FF4B4F"));

        TextView stockLow = (TextView) findViewById(R.id.tv_stock_low);
        SpannableString lowString = new SpannableString(String.format("最低 %s", decimalFormat.format(stock.low)));
        stockLow.setText(setSpans(lowString, "#17C296"));
    }

    private SpannableString setSpans(SpannableString target, String color) {
        ForegroundColorSpan startSpan = new ForegroundColorSpan(Color.BLACK);
        ForegroundColorSpan endSpan = new ForegroundColorSpan(Color.parseColor(color));
        target.setSpan(startSpan, 0, 2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        target.setSpan(endSpan, 2, target.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return target;
    }

}
