package com.lockscreen_video_recorder_uhudSoft.AdsFolder;

import android.app.Activity;
import android.content.Context;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;


public class facebook_Ad_interstitial {
    private final Activity activity;
    public static com.facebook.ads.InterstitialAd facebook_mInterstitialAd;
    public static InterstitialAdListener facebook_interstitialAdListener;
    public static int counter = 0;
    public static int total_counter = 1;

    public facebook_Ad_interstitial(Context context) {
        activity = (Activity) context;
        showInterstitialAd();
    }

    private void showInterstitialAd() {


        facebook_mInterstitialAd = new InterstitialAd(activity, "905200500907458_905201627574012");
        // Set listeners for the Interstitial Ad
        facebook_interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                //    Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                //  Log.e(TAG, "Interstitial ad dismissed.");
                counter = 0;
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                //   Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                facebook_mInterstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                // Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                // Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

    }
}
