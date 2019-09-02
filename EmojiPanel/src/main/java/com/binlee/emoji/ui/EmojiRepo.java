package com.binlee.emoji.ui;

import android.content.Context;

import com.binlee.emoji.helper.EmojiHelper;
import com.binlee.emoji.helper.LogHelper;
import com.binlee.emoji.helper.OsHelper;
import com.binlee.emoji.helper.ThreadHelper;
import com.binlee.emoji.model.DbManager;
import com.binlee.emoji.model.Emoji;
import com.binlee.emoji.model.EmojiGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created on 19-7-21.
 * <p>
 * 表情数据仓库，所有与表情相关的数据操作均与此类进行交互，不直接操作数据库
 *
 * @author leebin
 */
public final class EmojiRepo {
    
    private static final String TAG = "EmojiRepo";
    /**
     * 内存中缓存的表情数据，如果内存被回收，则重新从数据库读取
     */
    private static Map<EmojiGroup, Emoji[][]> sEmojiMap = new LinkedHashMap<>();
    /**
     * 内存中记录的最近使用表情，当数据更新到数据库之后将会清空
     */
    private static Set<Emoji> sRecordedUsedEmojis = new HashSet<>();
    /**
     * 是否是静态初始化
     */
    private static boolean sInit;
    /**
     * 数据源发生变化时更新 ui
     */
    private static List<OnDataChangeCallback> sCallbacks = new ArrayList<>();
    
    static {
        // 找个合适的时机预加载这个类以便提前初始化数据从而提升用户体验
        // 如果是首次安装，此方法执行大概在 380 ms 左右，所以预加载还是很有必要的
        // 非首次安装在 35 ms 左右
        final long start = LogHelper.logTime();
        sInit = true;
        preloadData();
        sInit = false;
        // 接着预加载图片资源？
        LogHelper.debug(TAG, "static setupClient: " + LogHelper.elapsedMillis(start) + "ms");
    }
    
    private EmojiRepo() {
        throw new AssertionError("no instance");
    }
    
    public static void staticInit() {
        // 为确保 EmojiRepo 类中的静态代码块执行
        // 注：单纯的加载一个类并不能使其静态代码块得以执行，需要调用一个静态的字段或者方法
    }
    
    private static void preloadData() {
        if (!OsHelper.isMasterProcess()) {
            return;
        }
        if (sEmojiMap == null) {
            sEmojiMap = new LinkedHashMap<>();
        }
        // 1, 查询所有分组
        // 2, 遍历分组查询组内所有表情
        // 3, 遍历组内表情并填充 sEmojiMap
        final Context context = OsHelper.app();
        // 首次升级新版表情初始化
        if (!EmojiHelper.hasGroup(EmojiHelper.getSmallGroupUuid())) {
            EmojiHelper.getInstance().initDefaultEmoji(context);
        }
        // 同步 server 数据
        EmojiHelper.getInstance().initEmojiGroupList(context);
        // 1, 查询所有分组
        List<EmojiGroup> groupList = EmojiHelper.getInstance().getCurrentEmojiGroupList(context);
        if (groupList == null || groupList.size() == 0) {
            return;
        }
        for (final EmojiGroup key : groupList) {
            final Emoji[][] pageData = getPageData(context, key, sInit);
            if (pageData == null) {
                continue;
            }
            // 填充 sEmojiMap
            emojiMap().put(key, pageData);
        }
    }
    
    // 不直接对外暴露
    private static Map<EmojiGroup, Emoji[][]> emojiMap() {
        if (sEmojiMap == null) {
            preloadData();
        }
        return sEmojiMap;
    }
    
    private static Set<Emoji> recordedEmojis() {
        if (sRecordedUsedEmojis == null) {
            sRecordedUsedEmojis = new HashSet<>();
        }
        return sRecordedUsedEmojis;
    }
    
    /**
     * 当表情数据发生变动时，执行更新逻辑
     * 1，新增或移除表情包（增量）；未实现
     * <p>
     * 2，新增或移除自定义表情（增量）；已实现
     * 3，自定义表情排序发生变化（增量）；已实现
     *
     * @return true 更新, false 没有更新
     */
    public static boolean updateEmojiMap(EmojiGroup group, boolean preload) {
        if (group == null) {
            throw new IllegalArgumentException("EmojiGroup is null.");
        }
        final Emoji[][] pageData = getPageData(OsHelper.app(), group, preload);
        if (pageData != null) {
            emojiMap().put(group, pageData);
            return true;
        }
        return false;
    }
    
    /**
     * 生成表情分页数据
     * @param preload 是否需要预加载数据
     */
    private static Emoji[][] getPageData(final Context context, final EmojiGroup key,
                                         final boolean preload) {
        // 2, 遍历分组查询组内所有表情
        final List<Emoji> emojis = EmojiHelper.getInstance().getEmojiList(context, key);
        if (emojis == null || emojis.size() == 0) {
            return null;
        }
        final Emoji[][] pageData;
        // 3, 遍历组内表情填充 sEmojiMap
        if (EmojiHelper.isSmallGroup(key)) {
            // 小表情每页3行7列, 每页的最后一个元素为`删除按钮`
            final int pageCount = (int) Math.ceil(emojis.size() / 20F);
            // 填充最近使用表情
            final List<Emoji> recentEmojis = EmojiHelper.getInstance().getRecentlyUsedEmojis();
            if (recentEmojis == null || recentEmojis.size() == 0) {
                pageData = new Emoji[pageCount][];
            } else {
                // 第一页数据要给最近使用表情留出位置
                pageData = new Emoji[pageCount + 1][];
                pageData[0] = new Emoji[recentEmojis.size()];
                for (int i = 0; i < recentEmojis.size(); i++) {
                    final Emoji emoji = recentEmojis.get(i);
                    emoji.setResId(EmojiHelper.getDrawableId(context, emoji.getKey()));
                    pageData[0][i] = emoji;
                }
            }
            final int startIndex = pageData.length == pageCount ? 0 : 1;
            for (int i = startIndex; i < pageData.length; i++) {
                final int start = (startIndex == 1 ? (i - 1) : i) * 20;
                final int end = start + 20 > emojis.size() ? emojis.size() : start + 20;
                // 最后一个是`删除按钮`
                pageData[i] = new Emoji[end - start + 1];
                for (int j = start, index = 0; j < end; j++, index++) {
                    final Emoji emoji = emojis.get(j);
                    if (preload) {
                        EmojiHelper.mapRes(context, emoji);
                    }
                    pageData[i][index] = emoji;
                    // 填充删除按钮
                    if (index == end - start - 1) {
                        pageData[i][index + 1] = EmojiHelper.createDelEmoji(context);
                    }
                }
            }
        } else {
            // 其他分组表情
            final int pageCount = (int) Math.ceil(emojis.size() / 8F);
            pageData = new Emoji[pageCount][];
            for (int i = 0; i < pageData.length; i++) {
                final int start = i * 8;
                final int end = start + 8 > emojis.size() ? emojis.size() : start + 8;
                // 无需删除按钮
                pageData[i] = new Emoji[end - start];
                for (int j = start, index = 0; j < end; j++, index++) {
                    final Emoji emoji = emojis.get(j);
                    if (preload) {
                        EmojiHelper.mapRes(context, emoji);
                    }
                    pageData[i][index] = emoji;
                }
            }
        }
        return pageData;
    }
    
    /**
     * 最近使用表情按使用时间倒序排列
     */
    private static void sortRecentEmojis(List<Emoji> emojis) {
        // l < r -> -1
        // l == r -> 0
        // l > r -> 1
        Collections.sort(emojis, (left, right) -> {
            if (left != null && right != null) {
                return Long.compare(right.getLastUseTime(), left.getLastUseTime());
            } else {
                return 0;
            }
        });
    }
    
    /**
     * 将内存中记录的值与原有的值合并(丢弃旧值，保留新值)
     *
     * @param list     新值
     * @param oldValue 旧值
     */
    private static void mergeOldValues(final List<Emoji> list, final Emoji[] oldValue) {
        for (final Emoji emoji : oldValue) {
            if (!list.contains(emoji)) {
                list.add(emoji);
            }
        }
    }
    
    /**
     * 更新最近使用表情
     *
     * @return 若有更新，返回更新后的数据；否则返回 null
     */
    public static Emoji[][] updateRecentEmojis() {
        final List<Emoji> cachedEmojis = new ArrayList<>(recordedEmojis());
        if (cachedEmojis.isEmpty()) {
            return null;
        }
        for (final EmojiGroup key : emojiMap().keySet()) {
            if (EmojiHelper.isSmallGroup(key)) {
                final Emoji[][] oldValue = emojiMap().remove(key);
                final Emoji[][] newValue;
                if (oldValue[0].length <= 9 && oldValue[0].length > 0) {
                    // 已有最近使用表情
                    newValue = new Emoji[oldValue.length][];
                    // copy 原数据
                    System.arraycopy(oldValue, 1, newValue, 1, oldValue.length - 1);
                    // 添加旧数据并去重
                    mergeOldValues(cachedEmojis, oldValue[0]);
                } else {
                    // 第一次有最近使用表情
                    newValue = new Emoji[oldValue.length + 1][];
                    // copy 原数据
                    System.arraycopy(oldValue, 0, newValue, 1, oldValue.length);
                }
                // 降序排列排序
                sortRecentEmojis(cachedEmojis);
                // 新增数据
                final int length = cachedEmojis.size() >= 9 ? 9 : cachedEmojis.size();
                newValue[0] = new Emoji[length];
                for (int i = 0; i < length; i++) {
                    newValue[0][i] = cachedEmojis.get(i);
                }
                // 缓存新数据
                emojiMap().put(key, newValue);
                // 清空内存
                recordedEmojis().clear();
                // 更新数据库
                ThreadHelper.runOnWorker(() -> {
                    // 最多更新 9 条数据
                    EmojiHelper.updateRecentlyUsedEmojis(cachedEmojis.subList(0, length));
                });
                return newValue;
            }
        }
        return null;
    }
    
    /**
     * 记录最近使用表情
     */
    public static void recordEmoji(final Emoji emoji) {
        // 新的要把旧的替换掉
        recordedEmojis().remove(emoji);
        recordedEmojis().add(emoji);
    }
    
    public static void updateLastUsedGroup(final List<EmojiGroup> groups) {
        // 更新内存
        for (final EmojiGroup key : emojiMap().keySet()) {
            for (final EmojiGroup group : groups) {
                if (Objects.equals(key, group)) {
                    key.setLastGroup(group.isLastGroup());
                    key.setLastChildIndex(group.getLastChildIndex());
                }
            }
        }
        // 更新数据库
        final int uid = EmojiHelper.getUserId();
        if (uid < 0) {
            return;
        }
        ThreadHelper.runOnWorker(() -> {
            DbManager.getInstance().updateLastUsedEmojiGroup(groups, uid);
        });
    }
    
    public static List<EmojiGroup> getEmojiGroups() {
        return new ArrayList<>(emojiMap().keySet());
    }
    
    public static Emoji[][] getGroupData(final EmojiGroup group) {
        return emojiMap().get(group);
    }
    
    public static void registerCallback(OnDataChangeCallback callback) {
        if (!sCallbacks.contains(callback)) {
            sCallbacks.add(callback);
        }
    }
    
    public static void unregisterCallback(OnDataChangeCallback callback) {
        sCallbacks.remove(callback);
    }
    
    /**
     * 添加或者移除表情包时回调
     *
     * @param group 表情分组, 当此值为 null 时，视为自定义表情发生变化, isAdd 参数不再处理
     * @param isAdd 新增 or 移除
     */
    public static void notifyDataChanged(final EmojiGroup group, final boolean isAdd) {
        // 先处理处理数据再回调？
        // 需要先更新内存中的数据，再回调
        // ThreadHelper.runOnWorker(() -> {
        //     // 更新数据
        //     // ...
        //     if (sCallbacks.size() == 0) {
        //         return;
        //     }
        //     ThreadHelper.runOnMain(() -> {
        //         // 刷新 ui
        //     });
        // });
        if (sCallbacks.size() == 0) {
            return;
        }
        for (final OnDataChangeCallback callback : sCallbacks) {
            if (group == null || EmojiHelper.isCustomGroup(group)) {
                callback.onCustomEmojiChanged();
            } else {
                callback.onGroupChanged(group, isAdd);
            }
        }
    }
    
    public interface OnDataChangeCallback {
        
        /**
         * 添加或者移除表情包时回调
         *
         * @param group 表情分组
         * @param isAdd 新增 or 移除
         */
        void onGroupChanged(final EmojiGroup group, final boolean isAdd);
    
        /**
         * 当自定义表情发生变化时回调
         */
        void onCustomEmojiChanged();
    }
}
