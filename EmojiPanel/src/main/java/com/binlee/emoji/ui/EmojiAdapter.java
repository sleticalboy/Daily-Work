package com.binlee.emoji.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.binlee.emoji.ImageAdapter;
import com.binlee.emoji.R;
import com.binlee.emoji.helper.UrlHelper;
import com.binlee.emoji.model.Emoji;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 19-7-21.
 *
 * @author leebin
 */
final class EmojiAdapter extends BaseAdapter {
    
    private final List<Emoji> mEmojis;
    private final int mSpanCount;
    private int mDeleteIndex = -1;
    private float mDeleteAlpha = 0;
    
    EmojiAdapter(final Emoji[] emojis, final int spanCount) {
        mEmojis = new ArrayList<>(Arrays.asList(emojis));
        mSpanCount = spanCount;
    }
    
    @Override
    public int getCount() {
        return mEmojis.size();
    }
    
    @Override
    public Emoji getItem(final int position) {
        return mEmojis.get(position);
    }
    
    @Override
    public long getItemId(final int position) {
        return position;
    }
    
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final Emoji emoji = getItem(position);
        final EmojiHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.emoji_item_layout, parent, false);
            holder = new EmojiHolder(convertView);
        } else {
            holder = (EmojiHolder) convertView.getTag();
        }
        if (emoji.getResId() != -1) {
            holder.emojiIcon.setImageResource(emoji.getResId());
        } else {
            final String model = UrlHelper.inspectUrl(emoji.getThumbnail());
            ImageAdapter.engine().show(model, holder.emojiIcon);
        }
        if (emoji.isSmall() || emoji.getDescription() == null) {
            holder.emojiName.setVisibility(View.GONE);
        } else {
            holder.emojiName.setVisibility(View.VISIBLE);
            holder.emojiName.setText(emoji.getDescription().getCn());
        }
        if (mDeleteIndex == position) {
            holder.delIcon.setVisibility(View.VISIBLE);
            holder.delIcon.setAlpha(mDeleteAlpha);
            holder.emojiIcon.setAlpha(1 - mDeleteAlpha);
        } else {
            if (emoji.isDelete()) {
                holder.delIcon.setVisibility(View.VISIBLE);
                holder.delIcon.setAlpha(1F);
                holder.emojiIcon.setVisibility(View.GONE);
            } else {
                holder.delIcon.setVisibility(View.GONE);
                holder.emojiIcon.setVisibility(View.VISIBLE);
                holder.emojiIcon.setAlpha(1F);
            }
        }
        final int size, width;
        if (mSpanCount == 4) {
            width = parent.getResources().getDisplayMetrics().widthPixels / 4;
            size = (int) (width * 0.67);
            final int id = emoji.isAdd() ? R.dimen.mx_dp_20 : R.dimen.mx_dp_8;
            if (emoji.isAdd()) {
                // 去掉按压效果(每页只有8条数据，可以不考虑重用的问题)
                holder.emojiIcon.setBackgroundResource(R.drawable.emoji_add_btn_cover);
                holder.emojiIcon.setColorFilter(Color.parseColor("#6C6C6C"));
            }
            final int padding = parent.getResources().getDimensionPixelSize(id);
            holder.emojiIcon.setPadding(padding, padding, padding, padding);
        } else {
            width = parent.getResources().getDisplayMetrics().widthPixels / 7;
            size = (int) (width * 0.75F);
        }
        final ViewGroup.LayoutParams lp = convertView.getLayoutParams();
        lp.width = width;
        convertView.setLayoutParams(lp);
        updateImageSize(holder.emojiIcon, size);
        updateImageSize(holder.delIcon, size);
        return convertView;
    }
    
    private void updateImageSize(final ImageView imageView, final int size) {
        final ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
        params.height = params.width = size;
        imageView.setLayoutParams(params);
    }
    
    void attachToViewPager(final ViewPager viewPager, final int index) {
        if (viewPager == null || viewPager.getAdapter() == null) {
            mDeleteIndex = -1;
            mDeleteAlpha = 0;
            return;
        }
        mDeleteIndex = index;
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(final int p, final float offset, final int offsetPixels) {
                if (p != 0) {
                    return;
                }
                // offset: [0, 1)
                // offset 增大：逐渐隐藏删除按钮
                // offset 减小：逐渐显示删除按钮
                mDeleteAlpha = 1 - offset;
                notifyDataSetChanged();
            }
        });
    }
    
    void updateDataSet(final Emoji[] values) {
        if (values != null) {
            mEmojis.clear();
            mEmojis.addAll(Arrays.asList(values));
            notifyDataSetChanged();
        }
    }
    
    Emoji[] getDataSet() {
        final Emoji[] dataSet = new Emoji[mEmojis.size()];
        mEmojis.toArray(dataSet);
        return dataSet;
    }
    
    private static class EmojiHolder {
        
        final ImageView emojiIcon;
        final ImageView delIcon;
        final TextView emojiName;
        
        EmojiHolder(final View convertView) {
            emojiIcon = convertView.findViewById(R.id.emojiIcon);
            delIcon = convertView.findViewById(R.id.delIcon);
            emojiName = convertView.findViewById(R.id.emojiName);
            convertView.setTag(this);
        }
    }
}
