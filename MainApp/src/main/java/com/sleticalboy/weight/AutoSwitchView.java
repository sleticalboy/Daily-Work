package com.sleticalboy.weight;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sleticalboy.dailywork.R;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 18-10-24.
 *
 * @author leebin
 */
public class AutoSwitchView extends ViewSwitcher implements ViewSwitcher.ViewFactory {

    private static final String TAG = "AutoSwitchView";

    private static final int START_ANIM = 1 << 1;
    private static final int STOP_ANIM = 1 << 2;

    private final List<ItemInfo> mItemInfos = new ArrayList<>();
    private int currentId = -1;
    private Handler handler;

    public AutoSwitchView(Context context) {
        this(context, null);
    }

    public AutoSwitchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public View makeView() {
        return View.inflate(getContext(), R.layout.auto_switic_item, null);
    }

    /**
     * 间隔时间
     *
     * @param time
     */
    public void setTextStillTime(final long time) {
        handler = new SwitchHandler(this, time);
    }

    public void setAnimTime(long duration) {
        setFactory(this);
        Animation in = new TranslateAnimation(0, 0, duration, 0);
        in.setDuration(duration);
        in.setInterpolator(new AccelerateInterpolator());
        Animation out = new TranslateAnimation(0, 0, 0, -duration);
        out.setDuration(duration);
        out.setInterpolator(new AccelerateInterpolator());
        setInAnimation(in);
        setOutAnimation(out);
    }

    public void setTextList(List<String> textList) {
        if (textList == null || textList.size() == 0) {
            return;
        }
        mItemInfos.clear();
        ItemInfo itemInfo;
        for (int i = 0, size = textList.size(); i < size; i += 2) {
            itemInfo = new ItemInfo();
            itemInfo.firstText = textList.get(i);
            if (i + 1 < size) {
                itemInfo.secondText = textList.get(i + 1);
            }
            mItemInfos.add(itemInfo);
        }
        currentId = -1;
    }

    public void start() {
        handler.sendEmptyMessage(START_ANIM);
    }

    public void stop() {
        handler.sendEmptyMessage(STOP_ANIM);
    }

    private void setupItemView(ItemInfo itemInfo) {
        Log.d(TAG, "setupItemView() called with: itemInfo = [" + itemInfo + "]");
        if (!(getNextView() instanceof LinearLayout)) {
            return;
        }
        final LinearLayout item = (LinearLayout) getNextView();
        ((TextView) item.getChildAt(0)).setText(itemInfo.firstText);
        if (itemInfo.secondText != null) {
            ((TextView) item.getChildAt(1)).setText(itemInfo.secondText);
        }
    }

    static final class SwitchHandler extends Handler {

        final SoftReference<View> mRefView;
        final long mTime;

        SwitchHandler(View view, long time) {
            mRefView = new SoftReference<>(view);
            mTime = time;
        }

        @Override
        public void handleMessage(Message msg) {
            final AutoSwitchView switchView = (AutoSwitchView) mRefView.get();
            switch (msg.what) {
                case START_ANIM:
                    if (switchView.mItemInfos.size() > 0) {
                        switchView.currentId++;
                        switchView.setupItemView(switchView.mItemInfos.get(switchView.currentId % switchView.mItemInfos.size()));
                    }
                    switchView.handler.sendEmptyMessageDelayed(START_ANIM, mTime);
                    break;
                case STOP_ANIM:
                    switchView.handler.removeMessages(START_ANIM);
                    break;
                default:
                    break;
            }
        }
    }

    private static final class ItemInfo {
        String firstText;
        String secondText;
    }
}
