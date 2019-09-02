package com.binlee.emoji.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.binlee.emoji.ImageAdapter;
import com.binlee.emoji.R;
import com.binlee.emoji.model.DbManager;
import com.binlee.emoji.model.Emoji;
import com.binlee.emoji.model.EmojiGroup;
import com.binlee.emoji.span.CustomAtSpan;
import com.binlee.emoji.span.EmojiSpan;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okio.Okio;

public class EmojiHelper {

    public static final String MX_EMOJI_GROUP_DEFAULT_UNIQUE_UUID = "mx_emoji_group_default_uuid";
    public static final String MX_EMOJI_DEFAULT_UNIQUE_UUID = "mx_emoji_default_uuid";
    private static final String MX_EMOJI_DEFAULT_DRAWABLE_PREFIX = "em_";
    public static final int MX_EMOJI_GROUP_DEFAULT_TYPE = 0x100;//默认小表情
    public static final int MX_EMOJI_GROUP_TYPE_CUSTOM = 1;//自定义表情
    public static final int MX_EMOJI_GROUP_TYPE_DEFAULT = 3;//预制表情包
    public static final int MX_EMOJI_GROUP_TYPE_STORE = 2;//商店表情包
    private static final int MAX_EMOJI_SIZE;

    private static final String REGEX = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::lt|/::\\$|/::X|/::Z|/::\\.\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::hg|/:\\|-\\)|/::!|/::L|/::gt|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:and-\\(|/:B-\\)|/:lt@|/:@gt|/::-O|/:gt-\\||/:P-\\(|/::\\.\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:ltWgt|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:em_1:/|/:em_2:/|/:em_3:/|/:em_4:/|/:em_5:/|/:em_6:/|/:em_7:/|/:em_8:/|/:em_9:/|/:em_10:/|/:em_11:/|/:em_12:/|/:em_13:/|/:em_14:/|/:em_15:/|/:em_16:/|/:em_17:/|/:em_18:/|/:em_19:/|/:em_20:/|/:em_21:/|/:em_22:/|/:em_23:/|/:em_24:/|/:em_25:/|/:em_26:/|/:em_27:/|/:em_28:/|/:em_29:/|/:em_30:/|/:em_31:/|/:em_32:/|/:em_33:/|/:em_34:/|/:em_35:/|/:em_36:/|/:em_37:/|/:em_38:/|/:em_39:/|/:em_40:/|/:em_41:/|/:em_42:/|/:em_43:/|/:em_44:/|/:em_45:/|/:em_46:/|/:em_47:/|/:em_48:/|/:em_49:/|/:em_50:/|/:em_51:/|/:em_52:/|/:em_53:/|/:em_54:/|/:em_55:/|/:em_56:/|/:em_57:/|/:em_58:/|/:em_59:/|/:em_60:/|/:em_61:/|/:em_62:/|/:em_63:/|/:em_64:/|/:em_65:/|/:em_66:/|/:em_67:/|/:em_68:/|/:em_69:/|/:em_70:/|/:em_71:/|/:em_72:/|/:em_73:/|/:em_74:/|/:em_75:/|/:em_76:/|/:em_77:/|/:em_78:/|/:em_79:/|/:em_80:/|/:em_81:/|/:em_82:/|/:em_83:/|/:em_84:/|/:em_85:/|/:em_86:/|/:em_87:/|/:em_88:/|/:em_89:/|/:em_90:/|/:em_91:/|/:em_92:/|/:em_93:/|/:em_94:/|/:em_95:/|/:em_96:/|/:em_97:/|/:em_98:/|/:em_99:/|/:em_100:/|/:em_101:/|/:em_102:/|/:em_103:/|/:em_104:/|/:em_105:/|/:em_106:/|/:em_107:/|/:em_108:/|/:em_109:/|/:em_110:/|/:em_111:/|/:em_112:/|/:em_113:/|/:em_114:/|/:em_115:/|/:em_116:/|/:em_117:/|/:em_118:/|/:em_119:/|/:em_120:/|/:em_121:/|/:em_122:/|/:em_123:/|/:em_124:/|/:em_125:/|/:em_126:/|/:em_127:/|/:em_128:/|/:em_129:/|/:em_130:/|/:em_131:/|/:em_132:/|/:em_133:/|/:em_134:/|/:em_135:/|/:em_136:/|/:em_137:/|/:em_138:/|/:em_139:/|/:em_140:/|/:em_141:/|/:em_142:/|/:em_143:/|/:em_144:/|/:em_145:/|/:em_146:/|/:em_147:/|/:em_148:/|/:em_149:/|/:em_150:/|/:em_151:/|/:em_152:/|/:em_153:/|/:em_154:/|/:em_155:/|/:em_156:/|/:em_157:/|/:em_158:/|/:em_159:/|/:em_160:/";
    private static final Pattern PATTERN = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    private static final String[] SMILEY_KEYS = new String[]{
            "/:em_1:/", "/:em_2:/", "/:em_3:/", "/:em_4:/", "/:em_5:/", "/:em_6:/", "/:em_7:/", "/:em_8:/", "/:em_9:/", "/:em_10:/", "/:em_11:/", "/:em_12:/", "/:em_13:/", "/:em_14:/", "/:em_15:/", "/:em_16:/", "/:em_17:/", "/:em_18:/", "/:em_19:/", "/:em_20:/",
            "/:em_21:/", "/:em_22:/", "/:em_23:/", "/:em_24:/", "/:em_25:/", "/:em_26:/", "/:em_27:/", "/:em_28:/", "/:em_29:/", "/:em_30:/", "/:em_31:/", "/:em_32:/", "/:em_33:/", "/:em_34:/", "/:em_35:/", "/:em_36:/", "/:em_37:/", "/:em_38:/", "/:em_39:/", "/:em_40:/",
            "/:em_41:/", "/:em_42:/", "/:em_43:/", "/:em_44:/", "/:em_45:/", "/:em_46:/", "/:em_47:/", "/:em_48:/", "/:em_49:/", "/:em_50:/", "/:em_51:/", "/:em_52:/", "/:em_53:/", "/:em_54:/", "/:em_55:/", "/:em_56:/", "/:em_57:/", "/:em_58:/", "/:em_59:/", "/:em_60:/",
            "/:em_61:/", "/:em_62:/", "/:em_63:/", "/:em_64:/", "/:em_65:/", "/:em_66:/", "/:em_67:/", "/:em_68:/", "/:em_69:/", "/:em_70:/", "/:em_71:/", "/:em_72:/", "/:em_73:/", "/:em_74:/", "/:em_75:/", "/:em_76:/", "/:em_77:/", "/:em_78:/", "/:em_79:/", "/:em_80:/",
            "/:em_81:/", "/:em_82:/", "/:em_83:/", "/:em_84:/", "/:em_85:/", "/:em_86:/", "/:em_87:/", "/:em_88:/", "/:em_89:/", "/:em_90:/", "/:em_91:/", "/:em_92:/", "/:em_93:/", "/:em_94:/", "/:em_95:/", "/:em_96:/", "/:em_97:/", "/:em_98:/", "/:em_99:/", "/:em_100:/",
            "/:em_101:/", "/:em_102:/", "/:em_103:/", "/:em_104:/", "/:em_105:/", "/:em_106:/", "/:em_107:/", "/:em_108:/", "/:em_109:/", "/:em_110:/", "/:em_111:/", "/:em_112:/", "/:em_113:/", "/:em_114:/", "/:em_115:/", "/:em_116:/", "/:em_117:/", "/:em_118:/", "/:em_119:/", "/:em_120:/",
            "/:em_121:/", "/:em_122:/", "/:em_123:/", "/:em_124:/", "/:em_125:/", "/:em_126:/", "/:em_127:/", "/:em_128:/", "/:em_129:/", "/:em_130:/", "/:em_131:/", "/:em_132:/", "/:em_133:/", "/:em_134:/", "/:em_135:/", "/:em_136:/", "/:em_137:/", "/:em_138:/", "/:em_139:/", "/:em_140:/",
            "/:em_141:/", "/:em_142:/", "/:em_143:/", "/:em_144:/", "/:em_145:/", "/:em_146:/", "/:em_147:/", "/:em_148:/", "/:em_149:/", "/:em_150:/", "/:em_151:/", "/:em_152:/", "/:em_153:/", "/:em_154:/", "/:em_155:/", "/:em_156:/", "/:em_157:/", "/:em_158:/", "/:em_159:/", "/:em_160:/"
    };
    private static final String[] SMILEY_TEXTS = new String[]{"[哈哈大笑]", "[笑哭]", "[亲亲哟]", "[一脸嫌弃]", "[眨眼]", "[哭了]", "[开心]", "[挤眉毛]", "[哦耶]", "[捂脸]", "[滑稽]", "[机智]", "[嘿嘿哟]", "[邪恶]",
            "[不开心]", "[可爱]", "[笑容]", "[色眯眯]", "[眼冒金星]", "[纠结]", "[流汗了]", "[呵呵]", "[尬]", "[舔嘴嘴]", "[眯眯眼]", "[思考一下]", "[气气的]", "[好怕哟]", "[别说话]", "[哇]", "[困困的]", "[要崩溃了]", "[沮丧流汗]", "[害羞了]",
            "[我都睡了]", "[翻白眼]", "[唉唉]", "[调皮]", "[酷酷的]", "[惊讶]", "[流口水]", "[见钱眼开]", "[哇哦]", "[哭唧唧]", "[有点难过]",
            "[轻微难过]", "[巨难过]", "[伤心的很]", "[难受]", "[难过了]", "[愤怒了]", "[懵了]", "[出洋相]", "[难过流汗]", "[带口罩]", "[伤心欲绝]", "[发烧]", "[头炸了]", "[感冒]", "[天使]", "[恶魔]", "[青蛙]", "[彩虹小马]", "[蜗牛]",
            "[小幽灵]", "[外星人]", "[兔兔]", "[捂眼小猴]", "[捂耳小猴]", "[偷笑小猴]", "[猪猪]", "[便便]", "[奶牛]", "[旺财]", "[笑脸猫咪]", "[色猫咪]", "[震惊猫咪]", "[狐狸]", "[老虎]", "[狮子]", "[否定之男]", "[否定之女]", "[ok之男]", "[ok之女]",
            "[举手之男]", "[举手之女]", "[兔女郎]", "[捂脸男人]", "[捂脸女人]", "[僵尸美女]", "[僵尸帅哥]", "[无奈男人]", "[无奈女人]", "[兔基友]", "[一家四口]", "[一家三口]", "[男婴]", "[女婴]", "[女人]", "[男人]", "[双手赞成]", "[鼓掌]", "[握手]", "[真好]", "[不喜欢]", "[拳头]",
            "[拼搏]", "[左勾拳]", "[右勾拳]", "[十]", "[剪刀手]", "[LOVEU]", "[上面]", "[下面]", "[二头肌]", "[瓦肯人你好]", "[左边]", "[右边]", "[拜托]", "[好呀]", "[女侦探]", "[女医生]", "[女博士]", "[男博士]", "[女领导]", "[男领导]", "[女科学家]", "[男科学家]", "[女王]", "[国王]", "[女圣诞老人]",
            "[圣诞老人]", "[警察]", "[工人]", "[警卫]", "[女人鱼]", "[男人鱼]", "[女吸血鬼]", "[男吸血鬼]", "[天使娃娃]", "[黑色月亮]",
            "[月亮脸]", "[月亮]", "[星星]", "[群星]", "[晴天]", "[四叶草]", "[多云]", "[阴天]", "[下雨]", "[雷阵雨]", "[雪]", "[水珠]",
            "[雨伞]", "[破壳]", "[小鸡]", "[雪人]", "[火]", "[闪电]", "[圣诞树]"
    };
    private static final String[] SMILEY_TEXTS_OLD = new String[]{"[微笑]", "[撇嘴]", "[色]", "[发呆]", "[得意]", "[流泪]", "[害羞]", "[闭嘴]", "[睡]", "[大哭]", "[尴尬]",
            "[发怒]", "[调皮]", "[呲牙]", "[惊讶]", "[难过]", "[酷]", "[冷汗]", "[抓狂]", "[吐]", "[偷笑]", "[可爱]", "[白眼]", "[傲慢]", "[饥饿]", "[困]", "[惊恐]", "[流汗]",
            "[憨笑]", "[悠闲]", "[奋斗]", "[咒骂]", "[疑问]", "[嘘]", "[晕]", "[疯了]", "[衰]", "[骷髅]", "[敲打]", "[再见]", "[擦汗]", "[抠鼻]", "[鼓掌]", "[糗大了]", "[坏笑]",
            "[左哼哼]", "[右哼哼]", "[哈欠]", "[鄙视]", "[委屈]", "[快哭了]", "[阴险]", "[亲亲]", "[吓]", "[可怜]", "[菜刀]", "[西瓜]", "[啤酒]", "[篮球]", "[乒乓]", "[咖啡]", "[饭]",
            "[猪头]", "[玫瑰]", "[凋谢]", "[示爱]", "[爱心]", "[心碎]", "[蛋糕]", "[闪电]", "[炸弹]", "[刀]", "[足球]", "[瓢虫]", "[便便]", "[月亮]", "[太阳]", "[礼物]", "[拥抱]",
            "[强]", "[弱]", "[握手]", "[胜利]", "[抱拳]", "[勾引]", "[拳头]", "[差劲]", "[爱你]", "[NO]", "[OK]"
    };

    private static EmojiHelper uniqueInstance = null;
    /**
     * key -> SimpleEmoji 映射
     */
    private static Map<String, SimpleEmoji> sItemMap = new HashMap<>();

    static {
        final DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        final float density = dm.density;
        MAX_EMOJI_SIZE = (int) (300 / 2 * density);
    }

    private EmojiHelper() {
    }

    public static EmojiHelper getInstance() {
        if (uniqueInstance == null) {
            synchronized (EmojiHelper.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new EmojiHelper();
                }
            }
        }
        return uniqueInstance;
    }

    /**
     * 更新表情包列表(未确定更新时机，主要同步表情包的名称和thumbnail)
     *
     * @param context
     */
    public void initEmojiGroupList(Context context) {
        // 从server获取数据
        final List<EmojiGroup> groups = new ArrayList<>();
        // 因为这里本就在子线程 所以直接同步更新数据即可
        for (EmojiGroup group : groups) {
            //
        }
    }

    private void updateEmojiGroupList(Context context, List<EmojiGroup> emojiGroups) {
        final int userId = getUserId();
        if (emojiGroups == null || emojiGroups.isEmpty() || userId < 0) {
            return;
        }
        final DbManager manager = DbManager.getInstance();
        for (EmojiGroup group : emojiGroups) {
            if (isSmallGroup(group)) {
                continue;
            }
            // 是否需要更新本地表情
            // case 1: updatedAt 字段不一致（是否取最新的时间？）
            // case 2: 本地无该分组数据
            final boolean needUpdate, newComing;
            final EmojiGroup localGroup = manager.queryEmojiGroupByUuid(group.getUuid(), userId);
            if (localGroup == null) {
                newComing = true;
                needUpdate = false;
                manager.insertEmojiGroup(group, userId);
            } else {
                newComing = false;
                // 这个判断是否准确？
                needUpdate = !Objects.equals(group.getUpdatedAt(), localGroup.getUpdatedAt());
                manager.updateEmojiGroupNameThumbnailCount(group, userId);
            }
            if (!needUpdate && !newComing) {
                continue;
            }
           // new WBEmojiService().getEmojiGroupDetail(group.getUuid(), new WBViewCallBack(context) {
           //     @Override
           //     public void success(final Object obj) {
           //         if (needUpdate) {
           //             manager.deleteEmojisByGroup(group.getUuid());
           //         }
           //         if (obj != null && obj instanceof WrappedEmoji) {
           //             WrappedEmoji wrappedEmoji = (WrappedEmoji) obj;
           //             final List<Emoji> result = wrappedEmoji.mEmojiList;
           //             manager.insertEmojiList(result, userId);
           //         }
           //     }
           //
           //     @Override
           //     public void failure(final MXError error) {
           //         // super.failure(error);
           //     }
           // });
        }
    }

    /**
     * 获取当前用户的表情包列表：按照小表情，自定义表情包，预置表情包，商店表情包顺序
     *
     * @param mContext
     * @return
     */
    public List<EmojiGroup> getCurrentEmojiGroupList(Context mContext) {
        List<EmojiGroup> list = DbManager.getInstance().queryAllEmojiGroup(getUserId());
        List<EmojiGroup> sortList = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            HashMap<Integer, EmojiGroup> map = new HashMap<>();
            for (EmojiGroup group : list) {
                if (TextUtils.isEmpty(group.getThumbnail())) {
                    group.setThumbnail(getDrawableUri(mContext, "mx_input_emoji_custom"));
                }
                switch (group.getType()) {
                    case MX_EMOJI_GROUP_TYPE_STORE:
                        sortList.add(group);
                        break;
                    default:
                        map.put(group.getType(), group);
                        break;
                }
            }
            //预置表情包
            if (map.containsKey(MX_EMOJI_GROUP_TYPE_DEFAULT)) {
                sortList.add(0, map.get(MX_EMOJI_GROUP_TYPE_DEFAULT));
            }
            //自定义表情
            if (map.containsKey(MX_EMOJI_GROUP_TYPE_CUSTOM)) {
                sortList.add(0, map.get(MX_EMOJI_GROUP_TYPE_CUSTOM));
            }
            //小表情
            if (map.containsKey(MX_EMOJI_GROUP_DEFAULT_TYPE)) {
                sortList.add(0, map.get(MX_EMOJI_GROUP_DEFAULT_TYPE));
            }

        }
        return sortList;
    }

    public static String getSmallGroupUuid() {
        return MX_EMOJI_GROUP_DEFAULT_UNIQUE_UUID + "_" + getUserId();
    }

    /**
     * 初始化默认表情:小表情
     *
     * @param context
     */
    public void initDefaultEmoji(Context context) {
        final int userId = getUserId();
        if (userId < 0) {
            return;
        }
        final String packUuid = MX_EMOJI_GROUP_DEFAULT_UNIQUE_UUID + "_" + userId;
        /* 小表情 */
        final List<Emoji> smallEmojis = new ArrayList<>();
        for (int i = 0; i < SMILEY_KEYS.length; i++) {
            Emoji emoji = new Emoji();
            emoji.setUuid(MX_EMOJI_DEFAULT_UNIQUE_UUID + "_" + userId + "_" + i);
            emoji.setSha1(MX_EMOJI_DEFAULT_UNIQUE_UUID + "_" + userId + "_" + i);
            Emoji.Desc description = new Emoji.Desc();
            description.setCn(SMILEY_TEXTS[i]);
            emoji.setDescription(description);
            emoji.setThumbnail(getDrawableUri(context, MX_EMOJI_DEFAULT_DRAWABLE_PREFIX + (i + 1)));
            emoji.setPackUuid(packUuid);
            emoji.setSortOrder(i);
            emoji.setKey(SMILEY_KEYS[i].replace("\\", ""));
            emoji.setReplaceKey(SMILEY_KEYS[i]);
            smallEmojis.add(emoji);
        }
        DbManager.getInstance().insertEmojiList(smallEmojis, userId);

        /* 小表情分组 */
        final EmojiGroup smallGroup = new EmojiGroup();
        smallGroup.setUuid(packUuid);
        smallGroup.setSha1(packUuid);
        smallGroup.setType(MX_EMOJI_GROUP_DEFAULT_TYPE);
        smallGroup.setThumbnail(getDrawableUri(context, "mx_input_smile_grey"));
        smallGroup.setCount(smallEmojis.size());
        DbManager.getInstance().insertEmojiGroup(smallGroup, userId);
    }

    /**
     * 获取自定义表情包的uuid
     *
     * @return
     */
    public String getCustomEmojiGroupUuid() {
        EmojiGroup group = DbManager.getInstance().queryEmojiGroupByType(MX_EMOJI_GROUP_TYPE_CUSTOM, getUserId());
        return group == null ? null : group.getUuid();
    }

    public static String getDrawableUri(Context context, String drawableName) {
        return "android.resource://" + context.getPackageName() + "/drawable/" + drawableName;
    }

    public static int getDrawableId(final Context ctx, final String resName) {
        if (TextUtils.isEmpty(resName)) {
            return -1;
        }
        final String name = resName.replace("/", "").replace(":", "");
        return ctx.getResources().getIdentifier(name, "drawable", ctx.getPackageName());
    }

    /**
     * 自定义表情列表
     *
     * @return
     */
    public List<Emoji> getCustomEmojiList() {
        final int userId = getUserId();
        if (userId < 0) {
            return null;
        }
        return DbManager.getInstance().queryCustomEmojis(userId);
    }

    /**
     * 获取自定义表情包
     *
     * @return
     */
    public static EmojiGroup getCustomGroup() {
        final int userId = getUserId();
        if (userId < 0) {
            return null;
        }
        return DbManager.getInstance().getCustomEmojiGroup(userId);
    }

    public String getCustomEmojiFromRaw(Context context, int rawId) {
        try {
            final InputStream is = context.getResources().openRawResource(rawId);
            return Okio.buffer(Okio.source(is)).readUtf8();
        } catch (Exception e) {
            return null;
        }
    }

    public String getFromAssets(Context context, String fileName) {
        try {
            final InputStream is = context.getAssets().open(fileName);
            return Okio.buffer(Okio.source(is)).readUtf8();
        } catch (Exception e) {
            return null;
        }
    }

    // 创建小表情的删除按钮
    public static Emoji createDelEmoji(final Context ctx) {
        final Emoji emoji = new Emoji();
        emoji.setDelete(true);
        emoji.setThumbnail(getDrawableUri(ctx, "emoji_del_btn_nor"));
        emoji.setKey("del_btn");
        emoji.setResId(getDrawableId(ctx, "emoji_del_btn"));
        return emoji;
    }

    // 创建自定义表情的添加按钮
    private static Emoji createAddEmoji(Context ctx) {
        final Emoji emoji = new Emoji();
        emoji.setDescription(new Emoji.Desc());
        emoji.setDelete(false);
        emoji.setThumbnail(getDrawableUri(ctx, "mx_emoji_custom_add"));
        emoji.setKey("add_btn");
        emoji.setResId(getDrawableId(ctx, "mx_emoji_custom_add"));
        return emoji;
    }

    public static boolean isCustomGroup(final EmojiGroup group) {
        return group != null && MX_EMOJI_GROUP_TYPE_CUSTOM == group.getType();
    }

    public static boolean isSmallGroup(final EmojiGroup group) {
        return group != null && MX_EMOJI_GROUP_DEFAULT_TYPE == group.getType();
    }

    public static boolean hasEmoji(final String sha1) {
        return DbManager.getInstance().hasCustomEmoji(sha1);
    }

    public static boolean hasGroup(final String uuid) {
        return DbManager.getInstance().hasEmojiGroup(uuid);
    }

    public List<Emoji> getEmojiList(Context context, EmojiGroup group) {
        final int uid = getUserId();
        if (uid == -1) {
            return null;
        }
        final List<Emoji> emojis = new ArrayList<>();
        if (isCustomGroup(group)) {
            // 自定义表情第一个是添加按钮
            emojis.add(0, createAddEmoji(context));
            final List<Emoji> result = DbManager.getInstance().queryCustomEmojis(uid);
            if (result != null) {
                emojis.addAll(result);
            }
        } else {
            final List<Emoji> result = DbManager.getInstance()
                    .queryEmojiListByGroupUuid(group.getUuid(), uid);
            if (result != null) {
                emojis.addAll(result);
            }
        }
        return emojis;
    }

    public List<Emoji> getRecentlyUsedEmojis() {
        final int uid = getUserId();
        if (uid == -1) {
            return null;
        }
        return DbManager.getInstance().queryRecentUsedEmojis(uid);
    }

    public static void updateRecentlyUsedEmojis(final List<Emoji> emojis) {
        final int uid = getUserId();
        if (uid == -1) {
            return;
        }
        DbManager.getInstance().updateRecentUsedEmojis(emojis, uid);
    }

    public static int getUserId() {
        return 0xff;
    }

    /**
     * 自定义表情排序（排序成功后直接将数据库原有的表情清空，再将新顺序的数据添加）
     *
     * @param moveList
     * @param data
     */
    public List<Emoji> moveToFirst(List<Emoji> moveList, List<Emoji> data) {
        if (moveList == null || moveList.isEmpty()) {
            return data;
        }
        if (data == null || data.isEmpty()) {
            return data;
        }

        for (Emoji move : moveList) {
            DATA:
            for (Emoji emoji : data) {
                if (EmojiHelper.getInstance().isEqual(move, emoji)) {
                    data.remove(emoji);
                    break DATA;
                }
            }
        }

        for (int j = moveList.size() - 1; j >= 0; j--) {
            Emoji move = moveList.get(j);
            data.add(0, move);
        }

        //更新数据库
        cleanUpAndUpdateCustomEmoji(data);
//        DBStoreHelper.getInstance(context).updateEmojiSortOrder(currentUserID, data);
        return data;
    }

    /**
     * 删除自定义表情
     *
     * @param currentUserID
     * @param deleteList
     * @param data
     * @return
     */
    public List<Emoji> deleteEmojis(int currentUserID, List<Emoji> deleteList, List<Emoji> data) {
        if (deleteList == null || deleteList.isEmpty()) {
            return data;
        }
        if (data == null || data.isEmpty()) {
            return data;
        }

        for (Emoji move : deleteList) {
            DATA:
            for (Emoji emoji : data) {
                if (EmojiHelper.getInstance().isEqual(move, emoji)) {
                    data.remove(emoji);
                    break DATA;
                }
            }
        }

        //删除数据库中的数据
        DbManager.getInstance().deleteEmojiList(currentUserID, deleteList);
        return data;
    }

    /**
     * 清空并更新自定义表情：将库中的自定义表情清空，添加新的表情【注意此处添加的表情按照list倒序添加】
     */
    public void cleanUpAndUpdateCustomEmoji(List<Emoji> data) {
        List<Emoji> deleteList = getCustomEmojiList();
        //清空自定义表情
        DbManager.getInstance().deleteEmojiList(getUserId(), deleteList);
        //倒序添加
        List<Emoji> newList = new ArrayList<>();
        for (int j = data.size() - 1; j >= 0; j--) {
            newList.add(data.get(j));
        }

        if (!newList.isEmpty()) {
            //重新添加自定义表情
            DbManager.getInstance().insertEmojiList(newList, getUserId());
        }
    }

    /**
     * 判断两个表情是否是同一个表情（通过uuid判断）
     *
     * @param emoji1
     * @param emoji2
     * @return
     */
    public boolean isEqual(Emoji emoji1, Emoji emoji2) {
        return emoji1 != null && emoji2 != null && !TextUtils.isEmpty(emoji1.getUuid()) && emoji1.getUuid().equals(emoji2.getUuid());
    }

    public static void mapRes(final Context ctx, final Emoji emoji) {
        if (emoji == null) {
            return;
        }
        if (emoji.isSmall() && !emoji.isAdd() && !TextUtils.isEmpty(emoji.getKey())) {
            // 映射资源 id 到 emoji 对象中；预加载 drawable 资源
            final String emojiKey = emoji.getKey();
            final int resId = getDrawableId(ctx, emojiKey);
            if (resId > 0) {
                emoji.setResId(resId);
                sItemMap.put(emojiKey, SimpleEmoji.create(emoji));
                final Drawable dr = ContextCompat.getDrawable(ctx, resId);
                if (dr != null) {
                    LogHelper.debug("EmojiHelper", "mapRes() preload drawable: " + emojiKey);
                }
            } else {
                LogHelper.debug("EmojiHelper", "not found res: " + emojiKey);
            }
        } else if (!emoji.isSmall() && !TextUtils.isEmpty(emoji.getThumbnail())) {
            // 预加载表情包资源, 目前只加载了缩略图，如有必要可添加加载原图的逻辑
            ImageAdapter.engine().preload(ctx, null, UrlHelper.inspectUrl(emoji.getThumbnail()));
        }
    }

    public static int[] figureOutEmojiSize(Emoji emoji) {
        if (emoji == null) {
            return null;
        }
        final int outWidth, outHeight;
        final int width = emoji.getWidth(), height = emoji.getHeight();
        if (width > MAX_EMOJI_SIZE || height > MAX_EMOJI_SIZE) {
            if (width > MAX_EMOJI_SIZE && height > MAX_EMOJI_SIZE) {
                if (width > height) {
                    // w > h
                    outWidth = MAX_EMOJI_SIZE;
                    // 缩小或放大 width * s = maxW
                    outHeight = computeSize(Math.round(height * MAX_EMOJI_SIZE / width));
                } else {
                    // h > w
                    outHeight = MAX_EMOJI_SIZE;
                    // 缩小或放大 height * s = maxH
                    outWidth = computeSize(Math.round(width * MAX_EMOJI_SIZE / height));
                }
            } else if (width > MAX_EMOJI_SIZE) {
                outWidth = MAX_EMOJI_SIZE;
                // 缩小或放大 width * s = maxW
                outHeight = computeSize(Math.round(height * MAX_EMOJI_SIZE / width));
            } else {
                outHeight = MAX_EMOJI_SIZE;
                // 缩小或放大 height * s = maxH
                outWidth = computeSize(Math.round(width * MAX_EMOJI_SIZE / height));
            }
        } else {
            outWidth = width;
            outHeight = height;
        }
        return new int[]{outWidth, outHeight};
    }

    private static int computeSize(int size) {
        return size > MAX_EMOJI_SIZE ? MAX_EMOJI_SIZE : size;
    }

    //////////////////////////// Emoji text API ////////////////////////////

    /**
     * 返回需要删除的表情的索引
     *
     * @param source 原始文本
     * @return 如果未找到合适的位置则返回 -1
     */
    public static int findDeleteIndex(String source) {
        final Matcher matcher = PATTERN.matcher(source);
        String smileyKey = null;
        while (matcher.find()) {
            smileyKey = matcher.group();
        }
        if (smileyKey != null) {
            final String end = source.substring(source.length() - smileyKey.length());
            if (smileyKey.equals(end)) {
                return source.lastIndexOf(smileyKey);
            }
        }
        return -1;
    }

    public static String getSmileText(String text) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Pattern p = Pattern.compile("(\\[[^\\]]*\\])");
        Matcher m = p.matcher(text);
        while (m.find()) {
            String smileText = m.group().substring(1, m.group().length() - 1);
            String smileKey = getSmileKeyBySmileText(smileText);
            sb.append(smileKey);
        }
        return sb.toString();
    }

    private static String getSmileKeyBySmileText(String smileText) {
        int index = -1;
        for (int i = 0; i < SMILEY_TEXTS.length; i++) {
            if (SMILEY_TEXTS[i].contains(smileText)) {
                index = i;
                break;
            }
        }
        return SMILEY_KEYS[index].replace("\\", "");
    }

    public static String getShowText(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Pattern p = Pattern.compile("(\\[[^\\]]*\\])");
        Matcher m = p.matcher(text);
        while (m.find()) {
            sb.append("[");
            sb.append(m.group(), 1, m.group().length() - 1);
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * 过滤表情(表情转为[大笑]这种文本格式)
     */
    public static String replaceSmileyCodeWithText(String orgText) {
        Matcher matcher = PATTERN.matcher(orgText);
        while (matcher.find()) {
            final SimpleEmoji emoji = sItemMap.get(matcher.group());
            orgText = orgText.replaceAll(emoji.replaceKey, emoji.text);
        }
        return orgText;
    }

    /**
     * 判断一个文本中是否包含表情
     *
     * @param text 文本
     * @return true 表示包含， false 表示不包含
     */
    public static boolean isContainSmiley(@NonNull CharSequence text) {
        Matcher matcher = PATTERN.matcher(text);
        while (matcher.find()) {
            String key = matcher.group();
            final SimpleEmoji emoji = sItemMap.get(key);
            if (emoji != null) {
                return true;
            }
        }
        return false;
    }

    //////////////////////////// Span or Bitmap API ////////////////////////////

    /**
     * 粘贴文本到输入框，同时将表情符号转换成 ImageSpan
     *
     * @param view EditText
     */
    public static void pasteToEditText(EditText view, String pasteString, int maxLength) {
        if (maxLength < 0) {
            maxLength = 1000;
        }
        final Matcher matcher = PATTERN.matcher(pasteString);
        String smileyKey;
        int start = 0;
        int smiley_start;
        while (matcher.find()) {
            smileyKey = matcher.group();
            smiley_start = matcher.start();
            if (smiley_start > start) {
                String pasteTextBefore = pasteString.substring(start, smiley_start);
                int index = view.getSelectionStart();
                view.getText().insert(index, pasteTextBefore);
                final int selection = index + pasteTextBefore.length();
                view.setSelection(selection > maxLength ? maxLength : selection);
            }
            final Spannable text = toSpannable(view.getContext(), smileyKey, view.getTextSize());
            int index = view.getSelectionStart();
            view.getText().insert(index, text);
            final int selection = index + text.length();
            view.setSelection(selection > maxLength ? maxLength : selection);
            start = matcher.end();
            if (selection > maxLength) {
                return;
            }
        }
        int index = view.getSelectionStart();
        String pasteTextAfter = pasteString.substring(start);
        if (maxLength > 0 && index + pasteTextAfter.length() > maxLength) {
            if (maxLength <= view.getText().length()) {
                return;
            }
            pasteTextAfter = pasteTextAfter.substring(0, maxLength - view.getText().length());
        }
        view.getText().insert(index, pasteTextAfter);
        view.setSelection(index + pasteTextAfter.length());
    }

    /**
     * 将源字符串中的表情符号转换成 ImageSpan 并返回 Spannable 对象
     */
    public static Spannable toSpannable(final Context context, final CharSequence source,
                                        final float textSize) {
        return toSpannable(context, source, 1, textSize, true);
    }

    /**
     * 将源字符串中的表情符号转换成 ImageSpan 并返回 Spannable 对象
     */
    public static Spannable toSpannable(final Context context, final CharSequence source,
                                        final float scale, final float textSize) {
        return toSpannable(context, source, scale, textSize, true);
    }

    /**
     * 将源字符串中的表情符号转换成 ImageSpan 并返回 SpannableString 对象
     *
     * @param scale       缩放比例
     * @param textSize    文本大小，自动优化时会根据文本大小自动调整表情大小
     * @param smartAdjust 是否自动优化表情大小
     */
    private static Spannable toSpannable(final Context context, final CharSequence source,
                                         final float scale, final float textSize,
                                         final boolean smartAdjust) {
        final Spannable result = prefillAtSpan(source);
        final float emojiSize = scale * textSize * (smartAdjust ? 1.3F : 1F);
        final Matcher matcher = PATTERN.matcher(result);
        try {
            while (matcher.find()) {
                final String key = matcher.group();
                final int start = matcher.start();
                final int end = start + key.length();
                // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                final Object what = EmojiHelper.createSpan(context, key, (int) emojiSize);
                // 计算该图片名字的长度，也就是要替换的字符串的长度
                // 将该图片替换字符串中规定的位置中
                result.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return result;
        } catch (Throwable e) {
            return result;
        }
    }

    private static ImageSpan createSpan(Context context, String key, int size) {
        final SimpleEmoji emoji = sItemMap.get(key);
        final Drawable dr = ContextCompat.getDrawable(context, emoji.resId);
        if (dr instanceof BitmapDrawable) {
            dr.setBounds(0, 0, size, size);
        }
        return new EmojiSpan(dr, emoji.key);
    }

    private static Spannable prefillAtSpan(final CharSequence source) {
        // 保留 source 中已有的 span
        final Spannable result = SpannableString.valueOf(source == null ? "" : source);
        // 取出要处理的文本
        final String text = result.toString();
        // 倒叙遍历会导致点击表情的删除按钮时将所有的 @someone 都被删除，所以改成正序遍历
        for (int st = 0, len = text.length(); st < len; st++) {
            if (text.charAt(st) == '@') {
                for (int en = st; en < len; en++) {
                    if (text.charAt(en) == '\u2005') {
                        result.setSpan(new CustomAtSpan(text.substring(st, en + 1)),
                                st, en + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static Bitmap smileyToBitmap(@NonNull Context context, int resId, float size) {
        final Drawable dr = ContextCompat.getDrawable(context, resId);
        if (dr instanceof BitmapDrawable) {
            dr.setBounds(0, 0, ((int) size), (int) size);
            return ((BitmapDrawable) dr).getBitmap();
        }
        return null;
    }

    private static Bitmap smileyToBitmap(@NonNull Context context, int resId) {
        final int size = context.getResources().getDimensionPixelSize(R.dimen.mx_dp_35);
        return smileyToBitmap(context, resId, size);
    }

    //////////////////////////// JS API ////////////////////////////

    public static String convertRichText(@NonNull Context context, @NonNull CharSequence text) {
        if (!isContainSmiley(text)) {
            return text.toString();
        } else {
            final List<SimpleEmoji> smileyList = findSmiley(text);
            for (final SimpleEmoji emoji : smileyList) {
                // 将文本中的表情替换成 img 标签
                text = text.toString().replace(emoji.key, smileyToImgTag(context, emoji.resId));
            }
            return text.toString();
        }
    }

    private static List<SimpleEmoji> findSmiley(@NonNull CharSequence text) {
        Matcher matcher = PATTERN.matcher(text);
        SimpleEmoji emoji;
        // 使用 HashSet 过滤掉重复的元素
        final Set<SimpleEmoji> smileySet = new HashSet<>();
        while (matcher.find()) {
            String key = matcher.group();
            emoji = sItemMap.get(key);
            if (emoji != null) {
                smileySet.add(emoji);
            }
        }
        return new ArrayList<>(smileySet);
    }

    /**
     * 将表情转换 img 标签
     *
     * @param context {@link Context}
     * @param resId   表情 resId
     * @return &lt;img src='data:image/jpeg;base64,base64_src_data'/&gt; 格式的 img 标签
     */
    private static String smileyToImgTag(@NonNull Context context, int resId) {
        final Bitmap bitmap = smileyToBitmap(context, resId);
        if (bitmap == null) {
            return "";
        }
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 参数100表示不压缩 0表示压缩到最小
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        final String base64Image = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
        return "<img src='data:image/jpeg;base64," + base64Image + "'/>";
    }

    /////////////// internal api ///////////////
    private static final class SimpleEmoji {
        final String key;
        final String replaceKey;
        final String text;
        final int resId;

        private SimpleEmoji(final Emoji src) {
            key = src.getKey();
            replaceKey = src.getReplaceKey();
            text = src.getDescription().getCn();
            resId = src.getResId();
        }

        static SimpleEmoji create(Emoji src) {
            return new SimpleEmoji(src);
        }
    }
}
