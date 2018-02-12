package com.sleticalboy.dailywork.bean;

import java.io.Serializable;

/**
 * Created on 18-2-12.
 *
 * @author sleticalboy
 * @version 1.0
 * @description
 */
public class AppInfo implements Serializable {

    public String category;
    public String name;
    public boolean isGroup;
    public int imgId;

    public AppInfo(String name, String category, int imgId, boolean isGroup) {
        this.category = category;
        this.name = name;
        this.imgId = imgId;
        this.isGroup = isGroup;
    }
}
