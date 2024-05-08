package com.lockscreen_video_recorder_uhudSoft.AdsFolder;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class admob_MyBanner {
    @SuppressLint("StaticFieldLeak")
    static AppCompatActivity context;
    AdView adView;

    public admob_MyBanner(Context context, AdView adView) {
        this.context = (AppCompatActivity) context;
        this.adView = adView;
        loadBanner();
    }

    public void loadBanner() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

//    private static AdSize getAdSize(FrameLayout frameView) {
//        Display display = context.getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        display.getMetrics(outMetrics);
//        float density = outMetrics.density;
//        float adWidthPixels = frameView.getWidth();
//        if (adWidthPixels == 0) {
//            adWidthPixels = outMetrics.widthPixels;
//        }
//        int adWidth = (int) (adWidthPixels / density);
//        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
//    }
}