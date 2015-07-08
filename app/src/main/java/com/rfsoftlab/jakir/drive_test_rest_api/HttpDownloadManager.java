package com.rfsoftlab.jakir.drive_test_rest_api;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.model.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * Created by admin on 7/8/2015.
 */
public class HttpDownloadManager {
    private String donwloadUrl;
    private String toFile;
    private FileDownloadProgressListener listener;
    private long totalBytes;
    private MainActivity mActivity;

    public void setListener(FileDownloadProgressListener listener) {
        this.listener = listener;
    }

    public HttpDownloadManager(MainActivity mActivity,File sourceFile, java.io.File destinationFile) {
        super();
        this.mActivity=mActivity;
        this.donwloadUrl = sourceFile.getDownloadUrl();
        this.toFile = destinationFile.toString();
        this.totalBytes = sourceFile.getFileSize();
    }

    public static interface FileDownloadProgressListener {
        public void downloadProgress(long bytesRead, long totalBytes);

        public void downloadFinished();

        public void downloadFailedWithError(Exception e);
    }

    public boolean download() {
        HttpResponse respEntity = null;
        try {
            // URL url = new URL(urlString);
            respEntity = mActivity.mService.getRequestFactory()
                    .buildGetRequest(new GenericUrl(donwloadUrl)).execute();
            InputStream in = respEntity.getContent();
            if(totalBytes == 0) {
                totalBytes = respEntity.getContentLoggingLimit();
            }
            try {
                FileOutputStream f = new FileOutputStream(toFile) {

                    @Override
                    public void write(byte[] buffer, int byteOffset,
                                      int byteCount) throws IOException {
                        // TODO Auto-generated method stub
                        super.write(buffer, byteOffset, byteCount);
                    }
            };
            byte[] buffer = new byte[1024];
            int len1 = 0;
            long bytesRead = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
                if (listener != null) {
                    bytesRead += len1;
                    listener.downloadProgress(bytesRead, totalBytes);
                }
            }
            f.close();
        } catch (Exception e) {
            if (listener != null) {
                listener.downloadFailedWithError(e);
            }
            return false;
        }
        if (listener != null) {
            listener.downloadFinished();
        }
        return true;

    } catch (IOException ex) {
        if (listener != null) {
            listener.downloadFailedWithError(ex);
            return false;
        }
    } finally {
        if(respEntity != null) {
            try {
                respEntity.disconnect();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    return false;
}
}
