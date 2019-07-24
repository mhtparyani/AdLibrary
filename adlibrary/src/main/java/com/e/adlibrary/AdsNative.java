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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    public AdsNative(Context context) {
        this.mContext = context;
        this.jsonUrl = "https://my-json-server.typicode.com/adeshsarwan/adserver-app-api-v1/native";
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
                if (mNativeAdListener != null) mNativeAdListener.onAdLoadFailed(new Exception("Null Response"));
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
                    if (mNativeAdListener != null) mNativeAdListener.onAdLoadFailed(e);
                }
            });
            /*Picasso.get().load(dialogModal.getIconUrl()).into(icon, new Callback() {
                @Override
                public void onSuccess() {
                    if (usePalette) {
                        Palette palette = Palette.from(((BitmapDrawable) (icon.getDrawable())).getBitmap()).generate();
                        int dominantColor = palette.getDominantColor(ContextCompat.getColor(mContext, R.color.colorAccent));

                        if (cta.getBackground() instanceof ColorDrawable) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) cta.setBackground(new GradientDrawable());
                            else cta.setBackgroundDrawable(new GradientDrawable());
                        }
                        GradientDrawable drawable = (GradientDrawable) cta.getBackground();
                        drawable.setColor(dominantColor);

                        if (dialogModal.getRating() > 0) {
                            ratings.setRating(dialogModal.getRating());
                            Drawable ratingsDrawable = ratings.getProgressDrawable();
                            DrawableCompat.setTint(ratingsDrawable, dominantColor);
                        } else ratings.setVisibility(View.GONE);
                    }


                    if (dialogModal.getLargeImageUrl().trim().isEmpty()) {
                        isAdLoaded = true;
                        if (mNativeAdListener != null) mNativeAdListener.onAdLoaded();
                    }
                }

                @Override
                public void onError(Exception e) {
                    isAdLoaded = false;
                    if (headerImage == null || dialogModal.getLargeImageUrl().isEmpty()) {
                        if (mNativeAdListener != null) mNativeAdListener.onAdLoadFailed(e);
                    }
                }
            });


            if (!dialogModal.getLargeImageUrl().trim().isEmpty())
                Picasso.get().load(dialogModal.getLargeImageUrl()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (headerImage != null) {
                            headerImage.setVisibility(View.VISIBLE);
                            headerImage.setImageBitmap(bitmap);
                        }
                        isAdLoaded = true;
                        if (mNativeAdListener != null) mNativeAdListener.onAdLoaded();
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        if (mNativeAdListener != null) mNativeAdListener.onAdLoadFailed(e);
                        isAdLoaded = false;
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            else {
                if (headerImage != null) headerImage.setVisibility(View.GONE);
            }*/

            title.setText(dialogModal.getAppTitle());
            description.setText(dialogModal.getAppDesc());

            /*if (ratings != null ) {
                ratings.setVisibility(View.VISIBLE);
                if (dialogModal.getRating() > 0) ratings.setRating(dialogModal.getRating());
                else ratings.setVisibility(View.GONE);
            }*/

        }

    }
}
