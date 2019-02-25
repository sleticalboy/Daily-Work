package com.sleticalboy.objectbox.bean.im;

import com.sleticalboy.objectbox.bean.BaseBean;

import java.io.Serializable;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.NameInDb;
import io.objectbox.annotation.Transient;
import io.objectbox.annotation.Uid;

@Entity
public class User extends BaseBean {

    private static final long serialVersionUID = -7874050789603872816L;
    @NameInDb("user_name")
    private String name;
    private Date birthday;
    @Transient // 不序列化
    private int gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + dbId +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
