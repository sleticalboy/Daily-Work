package com.sleticalboy.okhttp25.uploader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created on 2017/1/23 0023.
 *
 * @author Administrator
 */
public class Utils {

    private static ByteArrayPool mBytePool = new ByteArrayPool(4096);

    public static byte[] readStreamAsBytesArray(InputStream in, int readLength) throws IOException {
        if (in == null) {
            return new byte[0];
        } else {
            byte[] buffer = null;
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                buffer = mBytePool.getBuf(2048);

                int len;
                for (long readed = 0L; readed < (long) readLength && (len = in.read(buffer, 0, Math.min(2048, (int) ((long) readLength - readed)))) > -1; readed += (long) len) {
                    output.write(buffer, 0, len);
                }

                output.flush();
                return output.toByteArray();
            } finally {
                mBytePool.returnBuf(buffer);
            }
        }
    }

    public static byte[] readStreamAsBytesArray(RandomAccessFile in, int readLength) throws IOException {
        if (in == null) {
            return new byte[0];
        } else {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];

            int len;
            for (long readed = 0L; readed < (long) readLength && (len = in.read(buffer, 0, Math.min(2048, (int) ((long) readLength - readed)))) > -1; readed += (long) len) {
                output.write(buffer, 0, len);
            }

            output.flush();
            return output.toByteArray();
        }
    }


    /**
     * 判断网络是否联通
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (final NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED
                            && (anInfo.getType() == ConnectivityManager.TYPE_WIFI
                            || anInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                        Log.i("NetWorkState", "Availabel");
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
