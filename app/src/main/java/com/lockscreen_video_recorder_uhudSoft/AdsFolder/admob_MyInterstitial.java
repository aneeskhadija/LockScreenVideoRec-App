package com.lockscreen_video_recorder_uhudSoft.AdsFolder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.lockscreen_video_recorder_uhudSoft.R;

public class admob_MyInterstitial {

    private final Activity activity;
    public static InterstitialAd mInterstitialAd;
    private final AdRequest adRequest;

    public admob_MyInterstitial(Context context) {
        activity = (Activity) context;
        adRequest = new AdRequest.Builder().build();
    }

    public void showAdmobAd(Activity context) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(context);
        }

    }

    public void loadInterstitial() {

        InterstitialAd.load(activity,
                activity.getResources().getString(R.string.AdMob_InterstitialAd_ID),
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.d("AdsInitial", "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                mInterstitialAd = null;
                                Log.d("AdsInitial", "The ad was dismissed.");
                            }
                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                Log.d("AdsInitial", "The ad failed to show.");
                            }
                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                                Log.d("AdsInitial", "The ad was shown.");
                            }
                        });
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d("AdsError", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
}