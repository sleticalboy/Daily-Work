package com.binlee.emoji.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created on 19-7-16.
 *
 * @author leebin
 */
public class EmojiGroup implements Serializable {
    
    private static final long serialVersionUID = 2836324578616418084L;

    //表情和表情包的关联字段(唯一)
    private String uuid;
    private String name;
    private String description;
    private int size;
    private String sha1;
    private String path;
    private String thumbnail;
    private String surface;
    private String advertisement;
    @JSONField(name = "updated_at")
    private String updatedAt;
    private int count;
    private int type;

    /**
     * 切换到其他组时，当前组出于第几页
     */
    private int mLastChildIndex;
    /**
     * 退出重进时直接进入此分组，入库时 0 或 1
     */
    private boolean isLastGroup;
    
    public String getUuid() {
        return uuid == null ? "" : uuid;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
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
    
    public String getSurface() {
        return surface;
    }
    
    public void setSurface(String surface) {
        this.surface = surface;
    }
    
    public String getAdvertisement() {
        return advertisement;
    }
    
    public void setAdvertisement(String advertisement) {
        this.advertisement = advertisement;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public void setLastChildIndex(final int lastIndex) {
        mLastChildIndex = lastIndex;
    }
    
    public int getLastChildIndex() {
        return mLastChildIndex;
    }
    
    public void setLastGroup(final boolean lastGroup) {
        isLastGroup = lastGroup;
    }
    
    public boolean isLastGroup() {
        return isLastGroup;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EmojiGroup that = (EmojiGroup) o;
        return uuid.equals(that.uuid) && sha1.equals(that.sha1);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid, sha1);
    }
}
