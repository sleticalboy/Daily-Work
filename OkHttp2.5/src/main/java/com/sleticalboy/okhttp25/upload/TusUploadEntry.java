package com.sleticalboy.okhttp25.upload;

/**
 * Created on 16-6-2.
 * @author xiao
 */
public class TusUploadEntry {

    private final String ossKey;
    private final long fileSize;
    private String path;
    private String thumbPath;
    private String suffix;
    private String thumbSuffix;
    private String uploadId;

    public TusUploadEntry(String path,
                          String thumbPath,
                          String suffix,
                          String thumbSuffix,
                          String uploadId,
                          String ossKey,
                          long fileSize) {
        this.path = path;
        this.thumbPath = thumbPath;
        this.suffix = suffix;
        this.thumbSuffix = thumbSuffix;
        this.uploadId = uploadId;
        this.ossKey = ossKey;
        this.fileSize = fileSize;
    }

    public String getPath() {
        return path;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getThumbSuffix() {
        return thumbSuffix;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getOssKey() {
        return ossKey;
    }

    public long getFileSize() {
        return fileSize;
    }
}
