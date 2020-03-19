package com.sleticalboy.dailywork.csv;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.weight.AutoSwitchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-10-23.
 *
 * @author leebin
 */
public final class AutoSwitchUI extends BaseActivity {

    private AutoSwitchView autoSwitchView;

    @Override
    protected int layoutResId() {
        return R.layout.activity_auto_switch;
    }

    @Override
    protected void initView() {
        autoSwitchView = findViewById(R.id.auto_switch_view);
    }

    @Override
    protected void initData() {
        List<String> textList = new ArrayList<>();
        for (int i = 0; i < 31; i++) {
            textList.add("第 " + i + " 条数据");
        }
        autoSwitchView.setTextList(textList);
        autoSwitchView.setAnimTime(300);
        autoSwitchView.setTextStillTime(3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoSwitchView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoSwitchView.start();
    }
}
