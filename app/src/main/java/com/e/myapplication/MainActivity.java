package com.e.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.e.adlibrary.AdsNative;
import com.e.adlibrary.listener.NativeAdListener;
import com.google.android.material.button.MaterialButton;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.card_view).setVisibility(View.GONE);
        TextView loading = findViewById(R.id.loading);
        AdsNative houseAdsNative = new AdsNative(this, "https://www.lazygeniouz.com/houseAds/ads.json");
        houseAdsNative.setNativeAdView(findViewById(R.id.card_view));
        houseAdsNative.usePalette(true);
        houseAdsNative.setNativeAdListener(new NativeAdListener() {
            @Override
            public void onAdLoaded() {
                loading.setVisibility(View.GONE);
                findViewById(R.id.card_view).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdLoadFailed(Exception e) {
                loading.setText(String.format("%s%s", getString(R.string.ad_failed), e.getMessage()));
                loading.setVisibility(View.VISIBLE);
            }
        });

        MaterialButton load = findViewById(R.id.load);
        load.setOnClickListener(v -> {
            findViewById(R.id.card_view).setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            houseAdsNative.loadAds();
        });
    }
}
