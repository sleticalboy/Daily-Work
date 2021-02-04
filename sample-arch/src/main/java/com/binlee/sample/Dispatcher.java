package com.binlee.sample;

/**
 * Created on 21-2-4.
 *
 * @author binlee sleticalboy@gmail.com
 */
public class Dispatcher extends Thread implements IComponent {

    @Override
    public void ready() {
        super.start();
    }
}
