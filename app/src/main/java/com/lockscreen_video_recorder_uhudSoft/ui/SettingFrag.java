package com.lockscreen_video_recorder_uhudSoft.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.karumi.dexter.BuildConfig;
import com.lockscreen_video_recorder_uhudSoft.Prefs.SharedPrefs;
import com.lockscreen_video_recorder_uhudSoft.R;


public class SettingFrag extends Fragment {

    boolean isLoadingAd = false;
    LinearLayout cvVideoCamera, cvPreviewSize, cvVideoQuality, btnRateUs, btnShareApp;
    TextView txtVideoCameraSummary, txtPreviewSizeSummary, txtVideoQualitySummary;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    SharedPrefs prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        prefs = new SharedPrefs(requireContext());
        requireActivity().setTitle("Settings");



        // Facebook Load Banner Ad
        /*com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getActivity(), "905200500907458_905201104240731", AdSize.BANNER_HEIGHT_50);
        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) view.findViewById(R.id.banner_container);
        // Add the ad view to your activity layout
        adContainer.addView(adView);
        // Request an ad
        adView.loadAd();*/

        cvVideoCamera = view.findViewById(R.id.id_cvVideoCamera);
        //cvPreviewSize = view.findViewById(R.id.id_cvPreviewSize);
       // cvVideoQuality = view.findViewById(R.id.id_cvVideoQuality);
    //    btnUpdates = view.findViewById(R.id.id_btnCheckForUpdates);
        btnRateUs = view.findViewById(R.id.id_btnCheckRateUs);
        btnShareApp = view.findViewById(R.id.id_btnShare);
        txtVideoCameraSummary = view.findViewById(R.id.id_videoCameraSummary);
     //   txtPreviewSizeSummary = view.findViewById(R.id.id_previewSizeSummary);
      //  txtVideoQualitySummary = view.findViewById(R.id.id_videoQualitySummary);


        cvVideoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectCameraDialog();
            }
        });

        /*cvPreviewSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), VideoSettings.class));
                prefs.setStr("video_fragment", "preview_fragment");
            }
        });*/

        /*cvVideoQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), VideoSettings.class));
                prefs.setStr("video_fragment", "quality_fragment");
            }
        });*/

        btnRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rateUs();
            }
        });

        btnShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareApp();
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void showSelectCameraDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setTitle("Select Camera");
        String[] items = {"Back Camera","Front Camera"};
        String checkedItem = prefs.getStr("video_camera_value", "0");
        alertDialog.setSingleChoiceItems(items, Integer.parseInt(checkedItem),
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            txtVideoCameraSummary.setText("Back Camera");
                            prefs.setStr("video_camera_value", "0");
                            break;
                        case 1:
                            txtVideoCameraSummary.setText("Front Camera");
                            prefs.setStr("video_camera_value", "1");
                            break;
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void rateUs(){
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ requireActivity().getPackageName())));
        }
        catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+requireActivity().getPackageName())));
        }
    }

    private void shareApp(){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Lockscreen Video Recorder");
            String shareMessage= "\nLockscreen Video Recorder\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +
                    requireActivity().getPackageName() +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setVideoCamera(){
        if (prefs.getStr("video_camera_value", "0").equals("0")) {
            txtVideoCameraSummary.setText("Back Camera");
        } else {
            txtVideoCameraSummary.setText("Front Camera");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        setVideoCamera();
    }

}