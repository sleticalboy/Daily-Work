package com.sleticalboy.learning.data


/**
 * Created on 20-3-27.
 *
 * @author binlee sleticalboy@gmail.com
 */
class DataEngine private constructor() {

    private var mIndexModel: IndexModel? = null

    fun indexModel(): IndexModel {
        if (mIndexModel == null) {
            mIndexModel = IndexModel()
        }
        return mIndexModel!!
    }

    companion object {

        private var sDataEngine: DataEngine? = null
        fun get(): DataEngine {
            if (sDataEngine == null) sDataEngine = DataEngine()
            return sDataEngine as DataEngine
        }
    }
}