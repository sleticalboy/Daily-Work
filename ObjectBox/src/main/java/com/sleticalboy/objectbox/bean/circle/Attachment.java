package com.sleticalboy.objectbox.bean.circle;

import com.sleticalboy.objectbox.bean.BaseBean;

import io.objectbox.annotation.Entity;

@Entity
public class Attachment extends BaseBean {
    private static final long serialVersionUID = -6323511293880015819L;

    private String thumbnail;
    private String type;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "dbId=" + dbId +
                ", thumbnail='" + thumbnail + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
