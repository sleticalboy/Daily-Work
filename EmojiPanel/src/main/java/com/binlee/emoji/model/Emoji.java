package com.binlee.emoji.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created on 19-7-16.
 *
 * @author leebin
 */
public class Emoji implements Serializable, Cloneable {

    private static final long serialVersionUID = -45683397753071549L;

    //表情唯一标识
    private String uuid;
    @JSONField(serialize = false, deserialize = false)
    private String packUuid;
    private String name;
    private int size;
    private int height;
    private int width;
    private String sha1;
    private String path;
    // uri or url
    private String thumbnail;
    @JSONField(name = "updated_at")
    private String updatedAt;
    @JSONField(name = "description")
    private Desc description;
    @JSONField(name = "sort_order")
    private int sortOrder;
    private String key;
    private String replaceKey;
    /**
     * 使用时间，最近使用表情查询此字段，降序
     */
    @JSONField(serialize = false, deserialize = false)
    private long lastUseTime = -1L;
    @JSONField(serialize = false, deserialize = false)
    private boolean isDelete = false;
    /**
     * 小表情对应的本地资源 id
     */
    @JSONField(serialize = false, deserialize = false)
    private int resId = -1;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getSha1() {
        return sha1;
    }

    public void setSha1(String sha1) {
        this.sha1 = sha1;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDelete(final boolean delete) {
        isDelete = delete;
    }

    public boolean isDelete() {
        return isDelete || "del_btn".equals(key);
    }

    public void setPackUuid(final String groupUuid) {
        this.packUuid = groupUuid;
    }

    public String getPackUuid() {
        return packUuid;
    }

    public Desc getDescription() {
        return description;
    }

    public void setDescription(Desc description) {
        this.description = description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getReplaceKey() {
        return replaceKey;
    }

    public void setReplaceKey(String replaceKey) {
        this.replaceKey = replaceKey;
    }

    public long getLastUseTime() {
        return lastUseTime;
    }

    public void setLastUseTime(final long lastUseTime) {
        this.lastUseTime = lastUseTime;
    }

    // 小表情是打包在 apk 中的资源文件
    @JSONField(serialize = false, deserialize = false)
    public boolean isSmall() {
        return key != null || replaceKey != null;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isAdd() {
        return "add_btn".equals(key);
    }

    @Override
    public Emoji clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException ignored) {
        }
        // deep cloning
        return JSON.parseObject(JSON.toJSONString(this), Emoji.class);
    }

    // 最近使用表情利用 HashSet 去重，所以重写 equals 和 hashCode 方法
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Emoji emoji = (Emoji) o;
        return uuid.equals(emoji.uuid) && sha1.equals(emoji.sha1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, sha1);
    }

    public void setResId(final int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }

    public static class Desc implements Serializable {

        private static final long serialVersionUID = -5377411869795035352L;

        private String cn;
        private String en;

        public String getCn() {
            return cn;
        }

        public void setCn(String cn) {
            this.cn = cn;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }
    }
}
