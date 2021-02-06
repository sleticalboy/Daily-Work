package com.binlee.sample;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface AsyncCall extends Runnable {

    void onFinish();

    boolean isFinished();
}
