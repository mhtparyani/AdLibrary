/*
 * Created by Darshan Pandya.
 * @itznotabug
 * Copyright (c) 2018.
 */

package com.e.adlibrary;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.AnimRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.e.adlibrary.helper.JsonPullerTask;
import com.e.adlibrary.listener.AdListener;
import com.e.adlibrary.modal.InterstitialModal;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;

public class AdsInterstitial {
    private static Context mContext;
    private static AdListener mAdListener;
    private final String url;
    private int lastLoaded = 0;

    private static boolean isAdLoaded = false;
    private static Bitmap bitmap;
    private static String packageName;
    private static String imgUrl;

    public AdsInterstitial(Context context) {
        this.mContext = context;
        this.url = "https://my-json-server.typicode.com/adeshsarwan/adserver-app-api-v1/native";
    }

    public void setAdListener(AdListener adListener) {
        mAdListener = adListener;
    }

    public void loadAd() {
        if (url.trim().isEmpty()) throw new IllegalArgumentException("Url is Blank!");
        else {
            new JsonPullerTask(url, result -> {
                if (!result.trim().isEmpty()) setupAdd(result);
                else {
                    if (mAdListener != null) mAdListener.onAdLoadFailed(new Exception("Null Response"));
                }
            }).execute();
        }
    }

    public boolean isAdLoaded() {
        return isAdLoaded;
    }

    private void setUp(String val) {
        ArrayList<InterstitialModal> modalArrayList = new ArrayList<>();
        String x = new String(new StringBuilder().append(val));

        try {
            JSONObject rootObject = new JSONObject(x);
            JSONArray array = rootObject.optJSONArray("apps");

            for (int object = 0; object < array.length(); object++) {
                JSONObject jsonObject = array.getJSONObject(object);

                if (jsonObject.optString("app_adType").equals("interstitial")) {
                    InterstitialModal interstitialModal = new InterstitialModal();
                    interstitialModal.setInterstitialImageUrl(jsonObject.optString("app_interstitial_url"));
                    interstitialModal.setPackageOrUrl(jsonObject.optString("app_uri"));
                    modalArrayList.add(interstitialModal);
                }
            }

        } catch (JSONException e) { e.printStackTrace(); }

        if (modalArrayList.size() > 0) {
            final InterstitialModal modal = modalArrayList.get(lastLoaded);
            if (lastLoaded == modalArrayList.size() - 1) lastLoaded = 0;
            else lastLoaded++;

            Picasso.get().load(modal.getInterstitialImageUrl()).into(new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap resource, Picasso.LoadedFrom from) {
                    bitmap = resource;
                    if (mAdListener != null) mAdListener.onAdLoaded();
                    isAdLoaded = true;
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    if (mAdListener != null) mAdListener.onAdLoadFailed(e);
                    isAdLoaded = false;
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            packageName = modal.getPackageOrUrl();
        }
    }

    public void setupAdd(String result){

        String x = new String(new StringBuilder().append(result));

        try {
            JSONObject rootObject = new JSONObject(x);
            JSONObject link= rootObject.getJSONObject("link");
            JSONArray array = rootObject.optJSONArray("assets");

            for (int object = 0; object < array.length(); object++) {
                JSONObject jsonObject = array.getJSONObject(object);
                if (jsonObject.has("img")) {
                    JSONObject img= jsonObject.getJSONObject("img");
                    if (img.has("w")) {
                        if (img.optString("w").equals("320") && img.optString("h").equals("480")) {
                            imgUrl=img.getString("url");
                            if (mAdListener != null) mAdListener.onAdLoaded();
                                    isAdLoaded = true;
                            /*Picasso.get().load(img.getString("url")).into(new com.squareup.picasso.Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap resource, Picasso.LoadedFrom from) {
                                    bitmap = resource;
                                    if (mAdListener != null) mAdListener.onAdLoaded();
                                    isAdLoaded = true;
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    if (mAdListener != null) mAdListener.onAdLoadFailed(e);
                                    isAdLoaded = false;
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }

                            });*/

/*
                            GlideApp.with(mContext).load(img.getString("url")).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(new Target<Drawable>() {
                                @Override
                                public void onLoadStarted(@Nullable Drawable placeholder) {
                                    isAdLoaded = false;
                                }

                                @Override
                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                    //if (mAdListener != null) mAdListener.onAdLoadFailed();
                                    isAdLoaded = false;
                                }

                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    bitmap = ((BitmapDrawable)resource).getBitmap();
                                    if (mAdListener != null) mAdListener.onAdLoaded();
                                    isAdLoaded = true;
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    isAdLoaded = false;
                                }

                                @Override
                                public void getSize(@NonNull SizeReadyCallback cb) {
                                    isAdLoaded = false;
                                }

                                @Override
                                public void removeCallback(@NonNull SizeReadyCallback cb) {
                                    isAdLoaded = false;
                                }

                                @Override
                                public void setRequest(@Nullable Request request) {
                                    isAdLoaded = false;
                                }

                                @Nullable
                                @Override
                                public Request getRequest() {
                                    return null;
                                }

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onStop() {

                                }

                                @Override
                                public void onDestroy() {

                                }
                            });
*/
                        }
                    }
                }
            }
            packageName=link.getString("url");

        } catch (JSONException e) {
            e.printStackTrace(); }
    }
    public void show() {
        mContext.startActivity(new Intent(mContext, InterstitialActivity.class));
        if (mContext instanceof AppCompatActivity) ((AppCompatActivity) mContext).overridePendingTransition(0, 0);
    }


    @SuppressWarnings("unused")
    public void show(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        mContext.startActivity(new Intent(mContext, InterstitialActivity.class));
        if (mContext instanceof AppCompatActivity) ((AppCompatActivity) mContext).overridePendingTransition(enterAnim, exitAnim);
    }

    public static class InterstitialActivity extends Activity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (mAdListener != null) mAdListener.onAdShown();

            setContentView(R.layout.ads_interstitial_layout);
            ImageView imageView = findViewById(R.id.image);
            ImageButton button = findViewById(R.id.button_close);

            imageView.setImageBitmap(bitmap);
            Picasso.get().load(imgUrl).into(imageView);
//            GlideApp.with(mContext).load(imgUrl).apply(new RequestOptions()).into(imageView);
            imageView.setOnClickListener(view -> {
                isAdLoaded = false;
//                if (packageName.startsWith("http")) {
//                    Intent val = new Intent(Intent.ACTION_VIEW, Uri.parse(packageName));
//                    val.setPackage("com.android.chrome");
//                    if (val.resolveActivity(getPackageManager()) != null) startActivity(val);
//                    else startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(packageName)));
//
//                    if (mAdListener != null) mAdListener.onApplicationLeft();
//                    finish();
//                }
//                else {
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
//                        if (mAdListener != null) mAdListener.onApplicationLeft();
//                        finish();
//                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(packageName)));
                        if (mAdListener != null) mAdListener.onApplicationLeft();
                        finish();
//                    }
//                }
            });
            button.setOnClickListener(view -> {
                finish();
                isAdLoaded = false;
                if (mAdListener != null) mAdListener.onAdClosed();
            });
        }

        @Override
        public void onBackPressed() {
            isAdLoaded = false;
            if (mAdListener != null) mAdListener.onAdClosed();
            finish();
        }
    }
}
