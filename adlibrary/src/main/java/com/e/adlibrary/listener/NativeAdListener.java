/*
 * Created by Darshan Pandya.
 * @itznotabug
 * Copyright (c) 2018.
 */

package com.e.adlibrary.listener;

import android.view.View;

@SuppressWarnings("unused")
public interface NativeAdListener {

    void onAdLoaded();
    void onAdLoadFailed(Exception e);

    interface CallToActionListener{
        void onCallToActionClicked(View view);
    }
}
