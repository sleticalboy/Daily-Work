package com.sleticalboy.dailywork.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created on 18-3-5.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
@Entity
public class Note implements Serializable {

    private static final long serialVersionUID = -7099123589915693699L;

    @Id
    private Long id;
    @Property
    private String name;
    @Property
    @NotNull
    private String text;

    @Generated(hash = 1380736810)
    public Note(Long id, String name, @NotNull String text) {
        this.id = id;
        this.name = name;
        this.text = text;
    }
    @Generated(hash = 1272611929)
    public Note() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
}
