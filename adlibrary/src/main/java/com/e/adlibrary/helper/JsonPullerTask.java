/*
 * Created by Darshan Pandya.
 * @itznotabug
 * Copyright (c) 2018.
 */

package com.e.adlibrary.helper;

import android.os.AsyncTask;
import android.util.Log;

public class JsonPullerTask extends AsyncTask<String, String, String> {
    private final String jsonUrl;
    private final JsonPullerListener listener;

    public JsonPullerTask(String url, JsonPullerListener jsonPullerListener) {
        this.jsonUrl = url;
        this.listener = jsonPullerListener;
    }

    @Override
    protected String doInBackground(String... p1) {
        return AdsHelper.parseJsonObject(jsonUrl);
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onPostExecute(result);
        Log.d("Response", result);
    }

    public interface JsonPullerListener{
        void onPostExecute(String result);
    }
}
