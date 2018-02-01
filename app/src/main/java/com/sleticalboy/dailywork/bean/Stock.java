package com.sleticalboy.dailywork.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by AS on 18-1-29.
 *
 * @author sleticalboy
 * @version 1.0
 * @description 股票实体类
 */
public class Stock implements Serializable {

    public int errorNo;
    public String errorMsg;
    public List<DataEntity> data;

    public static class DataEntity {
        public String stockCode;
        public String stockName;
        public String exchange;
        public double close; // 今收-涨跌指数
        public double high; // 最高
        public double low; // 最低
        public double netChange; // 指数
        public double netChangeRatio; // 百分比
        public double preClose; // 昨收
        public double open; // 今开

        public long capitalization;
        public int asset;
        public int volume;
        public double amplitudeRatio;
        public String stockStatus;
        public double turnoverRatio;
        public int followNum;
    }
}
