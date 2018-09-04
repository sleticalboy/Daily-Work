package com.sleticalboy.okhttp25.uploader.custom;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 11/15/16.
 *
 * @author xiao
 */
@StringDef({UploadState.IDLE, UploadState.PAUSED, UploadState.CANCELED})
@Retention(RetentionPolicy.SOURCE)
public @interface UploadState {

    String IDLE = "custom_state_idle";
    String PAUSED = "custom_state_paused";
    String CANCELED = "custom_state_canceled";
}
