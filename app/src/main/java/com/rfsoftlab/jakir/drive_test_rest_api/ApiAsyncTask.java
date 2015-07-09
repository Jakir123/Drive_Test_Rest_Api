package com.rfsoftlab.jakir.drive_test_rest_api;

/**
 * Created by admin on 7/7/2015.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;

    /**
     * Constructor.
     *
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.e("On PostExecute", "executions over");
        mActivity.showMsg("All files are Up to date!!!");
    }

    /**
     * Background task to call Drive API.
     *
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearResultsText();
            mActivity.updateResultsText(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of up to 10 file names and IDs.
     *
     * @return List of Strings describing files, or an empty list if no files
     * found.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        List<String> fileInfo = new ArrayList<String>();
        FileList result = mActivity.mService.files().list().execute();
        List<File> files = result.getItems();
        Log.e("NO. OF FILE: ", files.size() + "");
        if (files != null) {
            for (File file : files) {
                if (file.getTitle().endsWith(".mxl") || file.getTitle().endsWith(".xml")) {
                    fileInfo.add(String.format("%s (%s)\n",
                            file.getTitle(), file.getId()));

//                Intent i=new Intent(mActivity.getBaseContext(), RetrieveContentsActivity.class);
//                i.putExtra("id",file.getId());
//                i.putExtra("title",file.getTitle());
//                mActivity.startActivity(i);

//                writeFileToSdCard(file,file.getTitle());
//                    Log.e("URL of file: ",file.getDownloadUrl()+"");
                    mActivity.download(file, file.getDownloadUrl());

//                downloadFile(file,file.getId(),file.size(),file.getTitle());

                }

            }
        }
        return fileInfo;
    }

    private void writeFileToSdCard(File file, String title) {
        java.io.File toFile = new java.io.File("/sdcard/" + title);

        try {
            toFile.createNewFile();
            InputStream inputStream = new FileInputStream(file + "");
            byte currentXMLBytes[] = inputStream.toString().getBytes();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(currentXMLBytes);
            OutputStream out = new FileOutputStream(toFile);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = byteArrayInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
                out.write('\n');
            }
            inputStream.close();
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadFile(File filee, String id, int size, String title) {

        File file = null;


        try {
            file = mActivity.mService.files().get(id).execute();
            java.io.File toFile = new java.io.File("/sdcard/" + title);
            toFile.createNewFile();
            HttpDownloadManager downloader = new HttpDownloadManager(mActivity, file, toFile);
            downloader.setListener(new HttpDownloadManager.FileDownloadProgressListener() {

                public void downloadProgress(long bytesRead, long totalBytes) {
                }

                @Override
                public void downloadFinished() {
                    // TODO Auto-generated method stub
                }

                @Override
                public void downloadFailedWithError(Exception e) {
                    // TODO Auto-generated method stub
                }
            });
            downloader.download();
            return;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}