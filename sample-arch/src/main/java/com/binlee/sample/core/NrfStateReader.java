package com.binlee.sample.core;

/**
 * Created on 21-2-25.
 *
 * @author binlee sleticalboy@gmail.com
 */
public final class NrfStateReader implements IComponent {

    private final IArchManager mArch;

    public NrfStateReader(IArchManager arch) {
        mArch = arch;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onDestroy() {
    }
}
