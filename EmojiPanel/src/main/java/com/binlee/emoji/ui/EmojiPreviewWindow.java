package com.binlee.emoji.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.binlee.emoji.ImageAdapter;
import com.binlee.emoji.R;
import com.binlee.emoji.helper.UrlHelper;


/**
 * Created on 19-7-21.
 *
 * @author leebin
 */
final class EmojiPreviewWindow extends PopupWindow {
    
    private final ImageView mImage;
    private final int mRight, mLeft;
    
    EmojiPreviewWindow(final Context context) {
        super(context);
        setClippingEnabled(false);
        final View content = View.inflate(context, R.layout.emoji_preview_layout, null);
        mImage = content.findViewById(R.id.emojiIcon);
        mImage.setCropToPadding(true);
        mImage.setPadding(32, 32, 32, 32);
        final ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) mImage.getLayoutParams();
        lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        mImage.setLayoutParams(lp);
        content.findViewById(R.id.emojiName).setVisibility(View.GONE);
        content.setClickable(false);
        content.setLongClickable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        content.setBackgroundColor(Color.TRANSPARENT);
        setContentView(content);
        mLeft = 16;
        mRight = content.getResources().getDisplayMetrics().widthPixels - 16;
    }
    
    private void updateInternal(final Params params) {
        setWidth(Math.max(params.width, params.height));
        setHeight(getWidth());
        final ViewGroup.LayoutParams lp = mImage.getLayoutParams();
        lp.width = lp.height = params.size;
        mImage.setLayoutParams(lp);
        ImageAdapter.engine().show(UrlHelper.inspectUrl(params.url), mImage);
        int resId = R.drawable.mx_emoji_preview_fg_center;
        if (params.position % 4 == 0) {
            resId = R.drawable.mx_emoji_preview_fg_left;
        } else if ((params.position + 1) % 4 == 0) {
            resId = R.drawable.mx_emoji_preview_fg_right;
        }
        mImage.setBackgroundResource(resId);
    }
    
    void show(View anchor, Params params) {
        dismiss();
        updateInternal(params);
        int x = params.loc[0];
        if (x <= mLeft) {
            x = mLeft;
        } else if (x + getWidth() >= mRight) {
            x = mRight - getWidth();
        }
        final int y = params.loc[1] - getHeight();
        showAtLocation(anchor, Gravity.NO_GRAVITY, x <= 0 ? 0 : x, y);
    }
    
    public static class Params {
        int position;
        // 表情尺寸
        int size;
        // 窗口宽高
        int width, height;
        String text, url;
        // 预览窗口显示位置
        final int[] loc = new int[2];
    }
    
    private static class BubbleDrawable extends Drawable {
        
        @Override
        public void draw(@NonNull final Canvas canvas) {
            canvas.drawColor(Color.BLUE);
        }
        
        @Override
        public void setAlpha(final int alpha) {
        }
        
        @Override
        public void setColorFilter(@Nullable final ColorFilter colorFilter) {
        }
        
        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }
    }
}
