package com.sleticalboy.sample.service;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 21-1-5.
 *
 * @author binli
 */
public class DataStruct implements Parcelable {

    public String mName;
    public boolean mNotify;

    public DataStruct() {
    }

    protected DataStruct(Parcel in) {
        mName = in.readStringNoHelper();
        mNotify = in.readByte() != 0;
    }

    public static final Creator<DataStruct> CREATOR = new Creator<DataStruct>() {
        @Override
        public DataStruct createFromParcel(Parcel in) {
            return new DataStruct(in);
        }

        @Override
        public DataStruct[] newArray(int size) {
            return new DataStruct[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringNoHelper(mName);
        dest.writeByte((byte) (mNotify ? 1 : 0));
    }

    @Override
    public String toString() {
        return "DataStruct{" +
                "mName='" + mName + '\'' +
                ", mNotify=" + mNotify +
                '}';
    }
}