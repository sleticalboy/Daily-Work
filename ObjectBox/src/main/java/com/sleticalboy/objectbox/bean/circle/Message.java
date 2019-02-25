package com.sleticalboy.objectbox.bean.circle;

import com.sleticalboy.objectbox.bean.BaseBean;
import com.sleticalboy.objectbox.bean.im.User;

import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Message extends BaseBean {

    private static final long serialVersionUID = -2357777662134407022L;
    private String richText;
    private String createAt;
    private ToOne<User> user;
    private ToMany<Attachment> attachments;

    public Message() {
        user = new ToOne<>(this, Message_.user);
        attachments = new ToMany<>(this, Message_.attachments);
    }

    public String getRichText() {
        return richText;
    }

    public void setRichText(String richText) {
        this.richText = richText;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public void setAttachments(final ToMany<Attachment> attachments) {
        this.attachments = attachments;
    }

    public ToMany<Attachment> getAttachments() {
        return attachments;
    }

    public void setUser(final ToOne<User> user) {
        this.user = user;
    }

    public ToOne<User> getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + dbId +
                ", richText='" + richText + '\'' +
                ", createAt='" + createAt + '\'' +
                '}';
    }
}
