package com.sleticalboy.greendao.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;
import java.util.Date;

/**
 * Created on 18-3-2.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
@Entity(indexes = {@Index(value = "text, date DESC", unique = true)})
public class StudentMsgBean implements Serializable {

    private static final long serialVersionUID = -690057300242434133L;

    @Id
    private Long id;

    @Property(nameInDb = "STUDENT_NUM")
    private String studentNum;
    @Property(nameInDb = "NAME")
    private String name;

    @NotNull
    private String text;
    private String comment;
    private java.util.Date date;

    @Convert(converter = StudentTypeConverter.class, columnType = String.class)
    private StudentType type;

    @Generated(hash = 2115483054)
    public StudentMsgBean(Long id, String studentNum, String name,
            @NotNull String text, String comment, java.util.Date date,
            StudentType type) {
        this.id = id;
        this.studentNum = studentNum;
        this.name = name;
        this.text = text;
        this.comment = comment;
        this.date = date;
        this.type = type;
    }

    @Generated(hash = 160565988)
    public StudentMsgBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentNum() {
        return this.studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
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

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public java.util.Date getDate() {
        return this.date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

    public StudentType getType() {
        return this.type;
    }

    public void setType(StudentType type) {
        this.type = type;
    }
}
