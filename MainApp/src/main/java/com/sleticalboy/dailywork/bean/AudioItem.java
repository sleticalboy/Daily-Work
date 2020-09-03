package com.sleticalboy.dailywork.bean;

import androidx.annotation.NonNull;

/**
 * Created on 20-9-3.
 *
 * @author Ben binli@grandstream.cn
 */
public class AudioItem {

    public String mTitle;
    public String mSummary;

    public int mColor;
    public int mColor2;

    @NonNull
    @Override
    public String toString() {
        return "AudioItem{" +
                "mTitle='" + mTitle + '\'' +
                ", mSummary='" + mSummary + '\'' +
                ", mColor=" + mColor +
                ", mColor2=" + mColor2 +
                '}';
    }
}
