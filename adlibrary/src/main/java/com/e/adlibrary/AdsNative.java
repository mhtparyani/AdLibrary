/*
 * Created by Darshan Pandya.
 * @itznotabug
 * Copyright (c) 2018.
 */

package com.e.adlibrary;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.palette.graphics.Palette;


import com.e.adlibrary.helper.AdsHelper;
import com.e.adlibrary.helper.JsonPullerTask;
import com.e.adlibrary.helper.RemoveJsonObjectCompat;
import com.e.adlibrary.listener.NativeAdListener;
import com.e.adlibrary.modal.DialogModal;
import com.e.adlibrary.modal.HouseAdsNativeView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.startapp.android.publish.ads.nativead.NativeAdDetails;
import com.startapp.android.publish.ads.nativead.NativeAdPreferences;
import com.startapp.android.publish.ads.nativead.StartAppNativeAd;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

@SuppressWarnings("unused")
public class AdsNative {
    private final Context mContext;
    private final String jsonUrl;

    private boolean usePalette = true;
    private boolean isAdLoaded = false;
    private boolean hideIfAppInstalled = false;
    private static int lastLoaded = 0;

    private HouseAdsNativeView nativeAdView;
    private View customNativeView;
    private NativeAdListener mNativeAdListener;
    private NativeAdListener.CallToActionListener ctaListener;
    private StartAppNativeAd startAppNativeAd;
    public AdsNative(Context context) {
        this.mContext = context;
        this.jsonUrl = "https://my-json-server.typicode.com/adeshsarwan/adserver-app-api-v1/native";
        StartAppSDK.init(context, "206252121", true);
        startAppNativeAd = new StartAppNativeAd(context);
    }

    public void setNativeAdView(HouseAdsNativeView nativeAdView) {
        this.nativeAdView = nativeAdView;
    }

    public void setNativeAdView(View view) {
        this.customNativeView = view;
    }

    public boolean isAdLoaded() {
        return isAdLoaded;
    }

    public void hideIfAppInstalled(boolean val) {
        this.hideIfAppInstalled = val;
    }

    public void usePalette(boolean usePalette) {
        this.usePalette = usePalette;
    }

    public void setNativeAdListener(NativeAdListener listener) {
        this.mNativeAdListener = listener;
    }

    public void setCallToActionListener(NativeAdListener.CallToActionListener listener) {
        this.ctaListener = listener;
    }

    public void loadAds() {
        isAdLoaded = false;
        if (jsonUrl.trim().isEmpty()) throw new IllegalArgumentException("Url is Blank!");
        else new JsonPullerTask(jsonUrl, result -> {
            if (!result.trim().isEmpty()) setUp(result);
            else {
                if (mNativeAdListener != null) {
                    mNativeAdListener.onAdLoadFailed(new Exception("Null Response"));
                    startAppNativeAd.loadAd(new NativeAdPreferences(),adListener);
                }
            }
        }).execute();
    }

    private void setUp(String response) {
        ArrayList<DialogModal> val = new ArrayList<>();

        try {
            JSONObject rootObject = new JSONObject(response);
            JSONArray array = rootObject.optJSONArray("assets");
            String title = null,rating= null,desc= null,downloads= null;
            for (int object = 0; object < array.length(); object++) {
                final JSONObject jsonObject = array.getJSONObject(object);

                if (jsonObject.has("img")){
                    JSONObject imgJSON= jsonObject.getJSONObject("img");
                    DialogModal dialogModal = new DialogModal();
                    dialogModal.setImgUrl(imgJSON.getString("url"));
                    dialogModal.setWidth(imgJSON.getString("w"));
                    dialogModal.setHeight(imgJSON.getString("h"));
                    val.add(dialogModal);
                }else {
                    if (jsonObject.has("title")){
                        JSONObject titleJSON= jsonObject.getJSONObject("title");
                        title=titleJSON.getString("text");
                    }
                    if (jsonObject.has("data")){
                        JSONObject dataJSON= jsonObject.getJSONObject("data");
                        if (dataJSON.getString("label").equalsIgnoreCase("description")){
                            desc=dataJSON.getString("value");
                        }
                        if (dataJSON.getString("label").equalsIgnoreCase("rating")){
                            rating=dataJSON.getString("value");
                        }
                        if (dataJSON.getString("label").equalsIgnoreCase("downloads")){
                            downloads=dataJSON.getString("value");
                        }

                    }
                }
            }
            for (DialogModal dialogModal: val){
                dialogModal.setAppTitle(title);
                dialogModal.setAppDesc(desc);
                dialogModal.setDownloads(downloads);
                dialogModal.setRating(rating);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (val.size() > 0) {
            final DialogModal dialogModal = val.get(lastLoaded);
            if (lastLoaded == val.size() - 1) lastLoaded = 0;
            else lastLoaded++;

            TextView title, description, price;
            final View cta;
            ImageView icon=null;
            ImageView headerImage = null;
            final RatingBar ratings;


            if (nativeAdView != null) {
                final HouseAdsNativeView view = nativeAdView;
                title = view.getTitleView();
                description = view.getDescriptionView();
                ratings = view.getRatingsView();
            } else {
                if (customNativeView != null) {
                    title = customNativeView.findViewById(R.id.houseAds_title);
                    description = customNativeView.findViewById(R.id.houseAds_description);
                    icon = (ImageView) customNativeView.findViewById(R.id.houseAds_app_icon);
                    headerImage = (ImageView) customNativeView.findViewById(R.id.houseAds_header_image);
                    ratings = customNativeView.findViewById(R.id.houseAds_rating);
                } else
                    throw new NullPointerException("NativeAdView is Null. Either pass HouseAdsNativeView or a View in setNativeAdView()");

            }
            if (dialogModal.getAppTitle().trim().isEmpty() || dialogModal.getAppDesc().trim().isEmpty())
                throw new IllegalArgumentException("Title & description should not be Null or Blank.");

            icon.setVisibility(View.INVISIBLE);
            headerImage.setVisibility(View.VISIBLE);
            headerImage.requestLayout();
            headerImage.getLayoutParams().height=Integer.parseInt(dialogModal.getHeight());
            headerImage.getLayoutParams().width=Integer.parseInt(dialogModal.getWidth());
            Picasso.get().load(dialogModal.getImgUrl()).into(headerImage, new Callback() {
                @Override
                public void onSuccess() {
                    isAdLoaded = true;
                    if (mNativeAdListener != null) mNativeAdListener.onAdLoaded();
                }

                @Override
                public void onError(Exception e) {
                    isAdLoaded = false;
                    if (mNativeAdListener != null){
                        mNativeAdListener.onAdLoadFailed(e);
                        startAppNativeAd.loadAd(new NativeAdPreferences(),adListener);
                    }
                }
            });


            title.setText(dialogModal.getAppTitle());
            description.setText(dialogModal.getAppDesc());
        }

    }

    AdEventListener adListener = new AdEventListener() {     // Callback Listener
        @Override
        public void onReceiveAd(Ad arg0) {
            // Native Ad received
            ArrayList<NativeAdDetails> ads = startAppNativeAd.getNativeAds();    // get NativeAds list
            // Print all ads details to log
            TextView title, description, price;
            final View cta;
            ImageView icon=null;
            ImageView headerImage = null;
            final RatingBar ratings;
            title = customNativeView.findViewById(R.id.houseAds_title);
            description = customNativeView.findViewById(R.id.houseAds_description);
            icon = (ImageView) customNativeView.findViewById(R.id.houseAds_app_icon);
            headerImage = (ImageView) customNativeView.findViewById(R.id.houseAds_header_image);
            ratings = customNativeView.findViewById(R.id.houseAds_rating);

            for (NativeAdDetails nativeAdDetails: ads){
                title.setText(nativeAdDetails.getTitle());
                description.setText(nativeAdDetails.getDescription());
                ratings.setRating(nativeAdDetails.getRating());
                Picasso.get().load(nativeAdDetails.getImageUrl()).into(headerImage);

            }
        }

        @Override
        public void onFailedToReceiveAd(Ad arg0) {
            // Native Ad failed to receive
            Log.e("MyApplication", "Error while loading Ad");
        }
    };
}
