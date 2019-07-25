package com.e.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.e.adlibrary.AdsNative;
import com.e.adlibrary.listener.NativeAdListener;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {
    TextView loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CardView cardView= (CardView) findViewById(R.id.card_view);
        //cardView.setVisibility(View.GONE);
        loading = findViewById(R.id.loading);
        AdsNative adsNative= new AdsNative(this);
        adsNative.setNativeAdView(cardView);
        adsNative.setNativeAdListener(new NativeAdListener() {
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
            adsNative.loadAds();
        });
    }
}
