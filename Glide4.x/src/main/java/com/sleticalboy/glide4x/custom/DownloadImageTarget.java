package com.sleticalboy.glide4x.custom;

import android.util.Log;

import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

/**
 * Created on 18-6-17.
 *
 * @author leebin
 * @description
 */
public class DownloadImageTarget extends BaseTarget<File> {

    @Override
    public void onResourceReady(File resource, Transition<? super File> transition) {
        Log.d("DownloadImageTarget", resource.getAbsolutePath());
    }

    @Override
    public void getSize(SizeReadyCallback cb) {
        cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    @Override
    public void removeCallback(SizeReadyCallback cb) {

    }
}
