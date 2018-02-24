package com.sleticalboy.dailywork.ui.activity;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sleticalboy.dailywork.R;
import com.sleticalboy.dailywork.base.BaseActivity;
import com.sleticalboy.dailywork.bean.AppInfo;
import com.sleticalboy.dailywork.weight.xrecycler.decoration.DividerGridItemDecoration;
import com.sleticalboy.dailywork.weight.xrecycler.decoration.SpaceDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-2-12.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class ClassifyActivity extends BaseActivity {

    public static final int TYPE_TITLE = 0x001;
    public static final int TYPE_CHILD = 0x002;

    private List<AppInfo> mAppList = new ArrayList<>();
    private static final int mId = R.mipmap.ic_launcher;
    private AppInfoAdapter mAdapter;

    @Override
    protected void initData() {
        List<AppInfo> appInfoList = new ArrayList<>();
        appInfoList.add(new AppInfo("", "common", -1, true));
        appInfoList.add(new AppInfo("找同事", "", mId, false));
        appInfoList.add(new AppInfo("敏行介绍", "", mId, false));
        appInfoList.add(new AppInfo("热度帮", "", mId, false));
        appInfoList.add(new AppInfo("敏邮", "", mId, false));
        appInfoList.add(new AppInfo("抽奖", "", mId, false));
        appInfoList.add(new AppInfo("后勤服务", "", mId, false));
        appInfoList.add(new AppInfo("签到", "", mId, false));
        appInfoList.add(new AppInfo("审批", "", mId, false));
        appInfoList.add(new AppInfo("找同事", "", mId, false));
        appInfoList.add(new AppInfo("敏行介绍", "", mId, false));
        appInfoList.add(new AppInfo("热度帮", "", mId, false));
        appInfoList.add(new AppInfo("敏邮", "", mId, false));
        appInfoList.add(new AppInfo("抽奖", "", mId, false));
        appInfoList.add(new AppInfo("后勤服务", "", mId, false));
        appInfoList.add(new AppInfo("签到", "", mId, false));
        appInfoList.add(new AppInfo("审批", "", mId, false));

        appInfoList.add(new AppInfo("", "work", -1, true));
        appInfoList.add(new AppInfo("公司公告", "", mId, false));
        appInfoList.add(new AppInfo("待办审批", "", mId, false));
        appInfoList.add(new AppInfo("OA演示", "", mId, false));
        appInfoList.add(new AppInfo("签到", "", mId, false));
        appInfoList.add(new AppInfo("IP电话", "", mId, false));
        appInfoList.add(new AppInfo("公司公告", "", mId, false));
        appInfoList.add(new AppInfo("待办审批", "", mId, false));
        appInfoList.add(new AppInfo("OA演示", "", mId, false));
        appInfoList.add(new AppInfo("签到", "", mId, false));
        appInfoList.add(new AppInfo("IP电话", "", mId, false));
        appInfoList.add(new AppInfo("敏邮", "", mId, false));
        appInfoList.add(new AppInfo("抽奖", "", mId, false));
        appInfoList.add(new AppInfo("后勤服务", "", mId, false));
        appInfoList.add(new AppInfo("签到", "", mId, false));
        appInfoList.add(new AppInfo("审批", "", mId, false));
        appInfoList.add(new AppInfo("找同事", "", mId, false));
        appInfoList.add(new AppInfo("敏行介绍", "", mId, false));
        appInfoList.add(new AppInfo("热度帮", "", mId, false));
        appInfoList.add(new AppInfo("敏邮", "", mId, false));

        appInfoList.add(new AppInfo("", "test", -1, true));
        appInfoList.add(new AppInfo("js api 兼容性", "", mId, false));
        appInfoList.add(new AppInfo("Web RTC 视频", "", mId, false));
        appInfoList.add(new AppInfo("H5 API Demo", "", mId, false));
        appInfoList.add(new AppInfo("js api 兼容性", "", mId, false));
        appInfoList.add(new AppInfo("Web RTC 视频", "", mId, false));
        appInfoList.add(new AppInfo("H5 API Demo", "", mId, false));
        appInfoList.add(new AppInfo("红包帐号应用", "", mId, false));
        appInfoList.add(new AppInfo("通知", "", mId, false));
        appInfoList.add(new AppInfo("百度", "", mId, false));
        appInfoList.add(new AppInfo("iboss", "", mId, false));
        appInfoList.add(new AppInfo("调试开关", "", mId, false));
        appInfoList.add(new AppInfo("话题", "", mId, false));
        appInfoList.add(new AppInfo("ADT", "", mId, false));
        appInfoList.add(new AppInfo("365 体验版", "", mId, false));

        appInfoList.add(new AppInfo("", "others", -1, true));
        appInfoList.add(new AppInfo("红包帐号应用", "", mId, false));
        appInfoList.add(new AppInfo("通知", "", mId, false));
        appInfoList.add(new AppInfo("百度", "", mId, false));
        appInfoList.add(new AppInfo("iboss", "", mId, false));
        appInfoList.add(new AppInfo("调试开关", "", mId, false));
        appInfoList.add(new AppInfo("话题", "", mId, false));
        appInfoList.add(new AppInfo("ADT", "", mId, false));
        appInfoList.add(new AppInfo("365 体验版", "", mId, false));
        appInfoList.add(new AppInfo("js api 兼容性", "", mId, false));
        appInfoList.add(new AppInfo("Web RTC 视频", "", mId, false));
        appInfoList.add(new AppInfo("H5 API Demo", "", mId, false));
        mAppList.addAll(appInfoList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // title 占 3 格，普通 item 占 1 格
                return mAdapter.getItemViewType(position) == TYPE_TITLE ? layoutManager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerGridItemDecoration(this, 8));

        mAdapter = new AppInfoAdapter(this, mAppList);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int attachLayout() {
        return R.layout.activity_classify;
    }

    static class AppInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<AppInfo> mList;
        Context mContext;
        LayoutInflater mInflater;

        public AppInfoAdapter(Context context, List<AppInfo> list) {
            mList = list;
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_TITLE) {
                return new ClassifyHolder(mInflater.inflate(R.layout.item_classify_title, parent, false));
            } else if (viewType == TYPE_CHILD) {
                return new AppInfoHolder(mInflater.inflate(R.layout.item_app_layout, parent, false));
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final AppInfo appInfo = mList.get(position);
            switch (holder.getItemViewType()) {
                case TYPE_TITLE:
                    final ClassifyHolder classifyHolder = (ClassifyHolder) holder;
                    classifyHolder.tv_classify_title.setText(appInfo.category);
                    classifyHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, appInfo.category, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case TYPE_CHILD:
                    final AppInfoHolder appInfoHolder = (AppInfoHolder) holder;
                    appInfoHolder.tv_app_name.setText(appInfo.name);
                    appInfoHolder.iv_app_icon.setImageResource(appInfo.imgId);
                    appInfoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mContext, appInfo.name, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public int getItemViewType(int position) {
            boolean isGroup = mList.get(position).isGroup;
            if (!isGroup) {
                return TYPE_CHILD;
            } else {
                return TYPE_TITLE;
            }
        }
    }

    static class AppInfoHolder extends RecyclerView.ViewHolder {

        ImageView iv_app_icon;
        TextView tv_app_name;

        public AppInfoHolder(View itemView) {
            super(itemView);
            iv_app_icon = itemView.findViewById(R.id.iv_app_icon);
            tv_app_name = itemView.findViewById(R.id.tv_app_name);
        }
    }

    static class ClassifyHolder extends RecyclerView.ViewHolder {

        TextView tv_classify_title;

        public ClassifyHolder(View itemView) {
            super(itemView);
            tv_classify_title = itemView.findViewById(R.id.tv_classify_title);
        }
    }
}
