package com.binlee.emoji.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.binlee.emoji.ImageAdapter;
import com.binlee.emoji.R;
import com.binlee.emoji.helper.EmojiHelper;
import com.binlee.emoji.helper.LogHelper;
import com.binlee.emoji.helper.ThreadHelper;
import com.binlee.emoji.helper.UiHelper;
import com.binlee.emoji.helper.UrlHelper;
import com.binlee.emoji.model.Emoji;
import com.binlee.emoji.model.EmojiGroup;
import com.binlee.emoji.span.CustomAtSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created on 19-7-17.
 *
 * @author leebin
 */
public final class EmojiPanel extends ConstraintLayout {
    
    private final LayoutInflater mInflater;
    private OnActionCallback mCallback;
    private EmojiPreviewWindow mPreviewWin;
    // 区分是点击切换分组还是由子 ViewPager 从最后一页或者第一页滑动切换分组
    private boolean mScrollToChange = true;
    // 退出表情面板时所在的组
    private EmojiGroup mLastGroup;
    private List<EmojiGroup> mGroups;
    private List<View> mGroupItems;
    private EmojiAdapter mRecentEmojiAdapter;
    /**
     * true: 只提供基础功能, 即只显示小表情
     * false: 提供全部功能，即除包含基础功能外的表情包、表情商店等入口
     */
    private boolean mBasicMode = true;
    private EditText mAttachedEditText;
    private EmojiRepo.OnDataChangeCallback mDataChangeCallback;
    
    public EmojiPanel(Context context) {
        this(context, null);
    }
    
    public EmojiPanel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public EmojiPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.emoji_panel_layout, this, true);
        setBackgroundColor(Color.WHITE);
    }
    
    /**
     * 如果对输入框的操作没有特殊需求，可直接调用此方法，内部自动处理删除和插入操作
     *
     * @param editText 输入框
     */
    public void attachWithEditText(final EditText editText) {
        initialize(editText, true, null);
    }
    
    /**
     * 如果对输入框的操作有特殊需求，比如处理`@`等特殊符号，直接调用此方法处理特殊操作
     *
     * @param editText 输入框
     * @param listener 回调，如调用者无特殊处理可传 null
     */
    public void initialize(final EditText editText, final boolean basicMode,
                           final OnActionCallback listener) {
        if (mCallback != null) {
            // mCallback 字段不为空说明 attachWithEditText() 或 initialize() 之一已被调用
            return;
        }
        mBasicMode = basicMode;
        mAttachedEditText = editText;
        mCallback = listener == null ? new OnActionCallback() : listener;
        // 首次 52 ms， 之后平均在 20 ms
        final long start = LogHelper.logTime();
        initContentView();
        LogHelper.debug("EmojiPanel", "Constructor setupClient:" + LogHelper.elapsedMillis(start));
    }
    
    private void initContentView() {
        final ImageButton emojiStore = findViewById(R.id.emojiStore);
        emojiStore.setOnClickListener(v -> {
            Toast.makeText(getContext(), "功能建设中", Toast.LENGTH_SHORT).show();
        });
        emojiStore.setVisibility(mBasicMode ? GONE : VISIBLE);
        final ImageButton myEmoji = findViewById(R.id.myEmoji);
        myEmoji.setOnClickListener(v -> {
            LogHelper.debug("EmojiPanel", "功能建设中");
        });
        myEmoji.setVisibility(mBasicMode ? GONE : VISIBLE);
        
        final ViewPager parentPager = findViewById(R.id.parentPager);
        final LinearLayout parentIndicator = findViewById(R.id.parentIndicator);
        setupEmojiGroup(parentPager, parentIndicator);
        findViewById(R.id.indicatorContainer).setVisibility(mBasicMode ? GONE : VISIBLE);
    }
    
    private void setupEmojiGroup(final ViewPager parentPager, final LinearLayout container) {
        mGroups = EmojiRepo.getEmojiGroups();
        if (mBasicMode) {
            final EmojiGroup smallGroup = mGroups.get(0);
            mGroups.clear();
            mGroups.add(smallGroup);
        }
        mGroupItems = new ArrayList<>();
        View item;
        String model;
        for (int i = 0; i < mGroups.size(); i++) {
            // 初始化组指示器
            final EmojiGroup emojiGroup = mGroups.get(i);
            item = mInflater.inflate(R.layout.emoji_preview_layout, container, false);
            final LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) item.getLayoutParams();
            lp.height = getResources().getDimensionPixelSize(R.dimen.mx_dp_36);
            lp.width = getResources().getDimensionPixelSize(R.dimen.mx_dp_48);
            lp.gravity = Gravity.CENTER;
            item.setLayoutParams(lp);
            item.setPadding(0, 0, 0, 0);
            item.setBackgroundResource(R.drawable.emoji_item_bg);
            item.findViewById(R.id.emojiName).setVisibility(GONE);
            final ImageView image = item.findViewById(R.id.emojiIcon);
            final ConstraintLayout.LayoutParams p = (LayoutParams) image.getLayoutParams();
            p.height = p.width = getResources().getDimensionPixelSize(R.dimen.mx_dp_28);
            p.bottomToBottom = LayoutParams.PARENT_ID;
            image.setLayoutParams(p);
            model = UrlHelper.inspectUrl(emojiGroup.getThumbnail());
            ImageAdapter.engine().show(model, image);
            final int index = i;
            item.setOnClickListener(v -> {
                mScrollToChange = false;
                parentPager.setCurrentItem(index);
            });
            container.addView(item);
            // 初始化组 item
            item = mInflater.inflate(R.layout.emoji_group_item_layout, parentPager, false);
            initGroupItem(item, emojiGroup);
            mGroupItems.add(item);
        }
        parentPager.setAdapter(new PagerAdapter() {
            @NonNull
            @Override
            public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
                // 根据分组不同渲染不同的 View
                final View groupItem = mGroupItems.get(position);
                container.addView(groupItem);
                return groupItem;
            }
    
            @Override
            public void destroyItem(@NonNull final ViewGroup container, final int position,
                                    @NonNull final Object object) {
                container.removeView((View) object);
            }
    
            @Override
            public int getCount() {
                return mGroups.size();
            }
    
            @Override
            public boolean isViewFromObject(@NonNull final View view, @NonNull final Object object) {
                return view == object;
            }
        });
        parentPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                // 分组指示器与 ViewPager 联动
                for (int i = 0, count = container.getChildCount(); i < count; i++) {
                    mGroups.get(i).setLastGroup(i == position);
                    container.getChildAt(i).setSelected(i == position);
                }
                // 当页面被选中的时候，取前一个组的 lastPage 字段
                // 如果 lastPage == 0 则设置当前组选中最后一页
                // 如果 lastPage == lastPager.mAdapter.mCount - 1 则选中第一页
                setChildPagerRealIndex(position);
            }
        });
        int index = -1;
        for (int i = 0, size = mGroups.size(); i < size; i++) {
            if (mGroups.get(i).isLastGroup()) {
                index = i;
                break;
            }
        }
        index = Math.max(index, 0);
        parentPager.setCurrentItem(index);
        container.getChildAt(index).setSelected(true);
    }
    
    private void setChildPagerRealIndex(final int position) {
        if (mScrollToChange && mLastGroup != null) {
            final ViewPager lastPager = mGroupItems.get(mGroups.indexOf(mLastGroup))
                    .findViewById(R.id.childPager);
            int realIndex = mLastGroup.getLastChildIndex();
            if (realIndex == 0) {
                realIndex = 99;
            } else if (lastPager.getAdapter() != null
                    && realIndex == lastPager.getAdapter().getCount() - 1) {
                realIndex = 0;
            }
            ((ViewPager) mGroupItems.get(position).findViewById(R.id.childPager))
                    .setCurrentItem(realIndex);
        }
        mLastGroup = mGroups.get(position);
        mScrollToChange = true;
    }
    
    private void initGroupItem(final View groupItem, final EmojiGroup group) {
        final Emoji[][] data = EmojiRepo.getGroupData(group);
        final int spanCount = EmojiHelper.isSmallGroup(group) ? 7 : 4;
        // 最近使用表情 3 列, 至多 9 条数据
        final boolean hasRecently = spanCount == 7 && data[0].length <= 9 && data[0].length > 0;
        final EmojiIndicator indicator = groupItem.findViewById(R.id.childIndicator);
        final ViewPager childPager = groupItem.findViewById(R.id.childPager);
        childPager.setAdapter(new PagerAdapter() {
    
            @NonNull
            @Override
            public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
                final View pageItem = mInflater.inflate(R.layout.emoji_page_item_layout, container, false);
                initPageData(childPager, pageItem, data[position], position, spanCount, hasRecently);
                container.addView(pageItem);
                return pageItem;
            }
    
            @Override
            public int getCount() {
                return data == null ? 0 : data.length;
            }
    
            @Override
            public boolean isViewFromObject(@NonNull final View view,
                                            @NonNull final Object object) {
                return view == object;
            }
    
            @Override
            public void destroyItem(@NonNull final ViewGroup container, final int position,
                                    @NonNull final Object object) {
                container.removeView((View) object);
            }
    
            @Override
            public float getPageWidth(final int position) {
                return position == 0 && hasRecently ? 4F / 7 : 1F;
            }
        });
        childPager.setCurrentItem(group.getLastChildIndex());
        childPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                group.setLastChildIndex(position);
                mLastGroup = group;
                if (position == 0) {
                    updateRecentlyUsed();
                }
            }
    
            @Override
            public void onPageScrolled(final int pos, final float offset, final int offsetPixels) {
                if ((pos == 0 || pos == data.length - 1) && offsetPixels == 0) {
                    mLastGroup = group;
                }
            }
        });
        indicator.attachViewPager(childPager, hasRecently);
    }
    
    private void initPageData(final ViewPager pager, final View pageItem, final Emoji[] data,
                              final int index, final int spanCount, final boolean hasRecently) {
        final int paddingTop = UiHelper.dip2px(spanCount == 7 ? 44 : 32);
        final EmojiGridView gridView = pageItem.findViewById(R.id.gridView);
        final EmojiAdapter adapter = new EmojiAdapter(data, spanCount);
        gridView.setAdapter(adapter);
        final TextView recently = pageItem.findViewById(R.id.recentlyUsed);
        final View divider = pageItem.findViewById(R.id.recentlyDivider);
        gridView.setBackgroundColor(Color.TRANSPARENT);
        gridView.setNumColumns(hasRecently && index == 0 ? 3 : spanCount);
        final int columnWidth = UiHelper.screenWidth() / spanCount;
        gridView.setColumnWidth(columnWidth);
        if (hasRecently && index == 0) {
            recently.setVisibility(VISIBLE);
            divider.setVisibility(VISIBLE);
            pageItem.setPadding(0, 0, 0, 0);
            gridView.setPadding(0, paddingTop, 0, 0);
            // 第一页的页宽是 ViewPager 页宽的 4/7
            // GridView 的宽度改为 ViewPager 页宽的 3/7
            final ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.width = columnWidth * 3;
            gridView.setLayoutParams(params);
            // 剩下的 1/7 页宽除以2,用作 divider 的 leftMargin
            final ConstraintLayout.LayoutParams lp = (LayoutParams) divider.getLayoutParams();
            lp.leftMargin = columnWidth / 2;
            divider.setLayoutParams(lp);
            mRecentEmojiAdapter = adapter;
        } else {
            recently.setVisibility(GONE);
            divider.setVisibility(GONE);
            pageItem.setPadding(0, paddingTop, 0, 0);
            gridView.setPadding(0, 0, 0, 0);
            final ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.width = columnWidth * spanCount;
            gridView.setLayoutParams(params);
        }
        if (hasRecently && index == 1) {
            // 第 2 页的第 17 个元素
            adapter.attachToViewPager(pager, 16);
        } else {
            adapter.attachToViewPager(null, -1);
        }
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 连续点击时
        gridView.setOnItemClickListener((parent, view, pos, id) -> {
            final Emoji src = adapter.getDataSet()[pos];
            // 深 copy，不改变原有对象字段
            final Emoji emoji = src.clone();
            // 拦截删除按钮
            if (hasRecently && index == 1 && pos == 16) {
                final View del = view.findViewById(R.id.delIcon);
                emoji.setDelete(del != null && del.getAlpha() >= 0.85F);
            } else {
                emoji.setDelete(src.isDelete());
            }
            if (emoji.isSmall() && !emoji.isDelete() && !emoji.isAdd()) {
                // 更新最后使用时间
                src.setLastUseTime(System.currentTimeMillis());
                recordRecentlyUsed(src);
            }
            emoji.setResId(src.getResId());
            handleClickEvent(emoji);
        });
        gridView.setOnPressListener(new EmojiGridView.OnPressListener() {
            @Override
            public void onLongPress(final int pos) {
                final Emoji emoji = adapter.getDataSet()[pos];
                if (emoji.isAdd()) {
                    // 如果长按是添加自定义表情按钮，则走点击的逻辑
                    // handleClickEvent(emoji);
                } else if (!emoji.isSmall()) {
                    handleLongTouchEvent(gridView, emoji, pos);
                } else {
                    //
                }
            }
        
            @Override
            public void onCancelPress() {
                dismissPopupWin();
            }
        });
    }
    
    private void handleClickEvent(final Emoji emoji) {
        if (emoji == null) {
            LogHelper.debug("EmojiPanel", "handleClickEvent() emoji is null");
            return;
        }
        final EditText inputView = mAttachedEditText;
        // 细化: 删除、插入、自定义表情、表情包
        // 优先处理特殊的`表情`，比如删除、添加等
        if (emoji.isDelete()) {
            // 如调用者处理了删除事件，则内部不再处理
            if (!mCallback.onDelete(emoji) && inputView != null) {
                // 删除的只能是小表情或者文字
                final int end = inputView.getSelectionEnd();
                if (end <= 0) {
                    return;
                }
                final Editable editable = inputView.getText();
                // 如果光标前一个字符是 '\u2005'
                if (editable.charAt(end - 1) == '\u2005') {
                    final CustomAtSpan[] spans = editable.getSpans(0, end, CustomAtSpan.class);
                    if (spans != null && spans.length > 0) {
                        // 取最后一个元素
                        final CustomAtSpan atSpan = spans[spans.length - 1];
                        final int st = editable.getSpanStart(atSpan);
                        editable.delete(st, end);
                    }
                } else {
                    String text = editable.toString();
                    if (end < text.length()) {
                        text = text.substring(0, end + 1);
                    }
                    final int index = EmojiHelper.findDeleteIndex(text);
                    editable.delete(index == 0 ? 0 : index > 0 ? index : end - 1, end);
                }
            }
        } else if (emoji.isAdd()) {
            // 如果是自定义表情的`添加按钮`
        } else if (!mCallback.onInsert(emoji) && emoji.isSmall()) {
            if (inputView != null) {
                final Spannable text = EmojiHelper.toSpannable(getContext(), emoji.getKey(), inputView.getTextSize());
                inputView.getText().insert(inputView.getSelectionEnd(), text);
            }
        } else {
            mCallback.onSpecialEmoji(emoji);
        }
    }
    
    private void handleLongTouchEvent(final GridView target, final Emoji emoji, final int index) {
        final View child;
        if (emoji.isDelete() || (child = target.getChildAt(index)) == null) {
            dismissPopupWin();
            return;
        }
        final EmojiPreviewWindow.Params params = new EmojiPreviewWindow.Params();
        params.position = index;
        child.getLocationInWindow(params.loc);
        params.loc[0] -= ((child.getPaddingLeft() + child.getPaddingRight()) / 2);
        params.width = child.getWidth();
        params.height = child.getHeight();
        params.size =  Math.min(params.width, params.height);
        params.text = emoji.getDescription() == null ? null : emoji.getDescription().getCn();
        params.url = TextUtils.isEmpty(emoji.getPath()) ? emoji.getThumbnail() : emoji.getPath();
        // LogHelper.debug("EmojiPanel", "handleLongTouchEvent() pos: " + index + ", emoji: "
        //         + emoji + ", position: {x: " + params.loc[0] + ", y: " + params.loc[1] + "}");
        if (mPreviewWin == null) {
            mPreviewWin = new EmojiPreviewWindow(getContext());
        }
        mPreviewWin.show(target, params);
    }
    
    private void dismissPopupWin() {
        if (mPreviewWin != null) {
            mPreviewWin.dismiss();
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EmojiRepo.registerCallback(mDataChangeCallback = new EmojiRepo.OnDataChangeCallback() {
            @Override
            public void onGroupChanged(final EmojiGroup group, final boolean isAdd) {
                final ViewPager parentPager = findViewById(R.id.parentPager);
                if (isAdd) {
                    final View item = mInflater.inflate(R.layout.emoji_group_item_layout, parentPager, false);
                    initGroupItem(item, group);
                    mGroupItems.add(item);
                    mGroups.add(group);
                } else {
                    final int index = mGroups.indexOf(group);
                    mGroupItems.remove(index);
                    mGroups.remove(group);
                }
                if (parentPager.getAdapter() != null) {
                    parentPager.getAdapter().notifyDataSetChanged();
                }
            }
    
            @Override
            public void onCustomEmojiChanged() {
                final EmojiGroup customGroup = mGroups.get(1);
                if (!EmojiRepo.updateEmojiMap(customGroup, false)) {
                    return;
                }
                final ViewPager parentPager = findViewById(R.id.parentPager);
                initGroupItem(mGroupItems.get(1), customGroup);
                if (parentPager.getAdapter() != null) {
                    parentPager.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }
    
    @Override
    protected void onDetachedFromWindow() {
        // 退出页面时才会回调此方法，由当前页跳转到其他页面时不会回调
        super.onDetachedFromWindow();
        EmojiRepo.unregisterCallback(mDataChangeCallback);
    }
    
    @Override
    protected void onVisibilityChanged(@NonNull final View changedView, final int v) {
        super.onVisibilityChanged(changedView, v);
        // 前台切后台时： invisible
        // 后台切前台时：visible
        // 退出时：先 invisible 再 onDetachedFromWindow()
        // 初始化时：先 onAttachedToWindow() 再 visible
        Log.d("EmojiPanel", "onVisibilityChanged() we update cache and db. "
                + (v == VISIBLE ? "VISIBLE" : v == INVISIBLE ? "INVISIBLE" : "GONE"));
        updateRecentlyUsed();
        if (v == VISIBLE) {
            return;
        }
        // 更新 mLastGroup
        // 没有放在 onDetachedFromWindow() 中执行是因为杀进程时 onDetachedFromWindow() 执行不到
        for (final EmojiGroup group : mGroups) {
            if (mLastGroup != null) {
                group.setLastGroup(group == mLastGroup);
            }
        }
        EmojiRepo.updateLastUsedGroup(mGroups);
    }
    
    // 更新缓存和数据库（最近使用表情）
    private void updateRecentlyUsed() {
        final Emoji[][] updatedValues = EmojiRepo.updateRecentEmojis();
        if (updatedValues == null) {
            return;
        }
        // 刷新 ui
        if (mRecentEmojiAdapter != null) {
            Log.d("EmojiPanel", "updateRecentlyUsed() updatedValues[0]:" + Arrays.toString(updatedValues[0]));
            mRecentEmojiAdapter.updateDataSet(updatedValues[0]);
        } else {
            // 如果是最开始的一次使用表情 mRecentEmojiAdapter 将会是 null
            // 此时需要重新初始化父 ViewPager 的第一个item
            ThreadHelper.runOnMain(() -> {
                final ViewPager parentPager = findViewById(R.id.parentPager);
                initGroupItem(mGroupItems.get(0), mGroups.get(0));
                if (parentPager.getAdapter() != null) {
                    parentPager.getAdapter().notifyDataSetChanged();
                }
            }, 350L);
        }
    }
    
    private void recordRecentlyUsed(final Emoji emoji) {
        EmojiRepo.recordEmoji(emoji);
    }
    
    public static class OnActionCallback {
    
        /**
         * @param emoji 被点的表情
         * @return 返回 true 表示调用者自己处理删除事件，反之内部默认处理
         */
        public boolean onInsert(final Emoji emoji) {
            return false;
        }
    
        /**
         * 当点击了表情删除按钮
         *
         * @param emoji 被点的表情
         * @return 返回 true 表示调用者自己处理删除事件，反之内部默认处理
         */
        public boolean onDelete(final Emoji emoji) {
            return false;
        }
        
        /**
         * 当点击表情包表情中或自定义表情
         *
         * @param emoji 被点表情
         */
        public void onSpecialEmoji(final Emoji emoji) {
        }
    }
}
