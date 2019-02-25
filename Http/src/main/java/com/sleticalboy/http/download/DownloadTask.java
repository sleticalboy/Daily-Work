package com.sleticalboy.http.download;

import com.sleticalboy.http.Task;

/**
 * Created on 18-9-19.
 *
 * @author leebin
 */
public final class DownloadTask extends Task {
    
    private static final long serialVersionUID = 5576329918184685085L;
    
    public DownloadTask() {
        super("minxing %s", "DownloadTask");
    }
    
    @Override
    public void execute() {
    
    }
    
    @Override
    public void cancel() {
    
    }
    
    @Override
    public boolean isCanceled() {
        return false;
    }
}
