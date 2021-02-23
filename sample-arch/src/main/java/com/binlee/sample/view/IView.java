package com.binlee.sample.view;

import com.binlee.sample.core.DataSource;

/**
 * Created on 21-2-23.
 *
 * @author binlee sleticalboy@gmail.com
 */
public interface IView {

    void onClearInfo(DataSource.Device device, boolean remote);
}
