package com.binlee.sample;

import android.content.Context;
import android.os.Handler;

import com.binlee.sample.event.IEvent;
import com.binlee.sample.util.Logger;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IFunctions {

    void init(Context context);

    void onStart();

    void onDestroy();

    Handler handler();

    void postEvent(IEvent event);

    Logger logger();
}
