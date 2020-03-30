package com.sleticalboy.dailywork.data;

/**
 * Created on 20-3-27.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class DataEngine {

    private static DataEngine sDataEngine;
    private IndexModel mIndexModel;

    private DataEngine() {
        // no instance
    }

    public static DataEngine get() {
        if (sDataEngine == null) {
            sDataEngine = new DataEngine();
        }
        return sDataEngine;
    }

    public IndexModel indexModel() {
        if (mIndexModel == null) {
            mIndexModel = new IndexModel();
        }
        return mIndexModel;
    }
}
