package com.sleticalboy.okhttp25.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 15/7/22.
 *
 * @author zhangll
 */
public class OSSManager {

    private String folder = "test";

    public static void postUploadProgress(int manuscriptId, long uploadedSize, long clipsTotalSize) {
    }

    public String getFolder() {
        return folder;
    }

    public void downloadObject(File localFile, String objectKey) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(localFile);
            // 获取文件输入流
            InputStream inputStream = new FileInputStream(objectKey);
            byte[] buffer = new byte[2048];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "NxOssTransferTaskManager{" +
                ", folder='" + folder + '\'' +
                '}';
    }

}
