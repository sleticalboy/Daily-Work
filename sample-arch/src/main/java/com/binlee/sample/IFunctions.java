package com.binlee.sample;

import android.os.Handler;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IFunctions {

    void init();

    void onStart();

    void onDestroy();

    Handler handler();

    void postEvent(IEvent event);

    Logger logger();
}
