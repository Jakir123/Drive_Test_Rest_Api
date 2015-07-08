package com.rfsoftlab.jakir.drive_test_rest_api;

import android.os.AsyncTask;

/**
 * Created by admin on 7/8/2015.
 */
public class DownlaodAsyncTask  extends AsyncTask<String, String, String> {
    private MainActivity mActivity;
    public DownlaodAsyncTask(MainActivity mActivity) {
        this.mActivity=mActivity;
    }


    @Override
    protected String doInBackground(String... params) {
        return null;
    }
}
