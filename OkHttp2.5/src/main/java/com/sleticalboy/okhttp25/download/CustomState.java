package com.sleticalboy.okhttp25.download;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 11/15/16.
 *
 * @author xiao
 */
@StringDef({CustomState.IDLE, CustomState.PAUSED, CustomState.CANCELED})
@Retention(RetentionPolicy.SOURCE)
public @interface CustomState {

    String IDLE = "custom_state_idle";
    String PAUSED = "custom_state_paused";
    String CANCELED = "custom_state_canceled";
}
